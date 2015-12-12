package com.knight.core.pagination;

import com.knight.core.util.DateUtils;
import com.knight.core.util.reflection.ConvertUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.hibernate.mapping.Collection;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.Assert;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;

/**
 * 与具体ORM实现无关的属性过滤条件封装类，主要记录页面中简单的搜索过滤条件. 用于页面表单传入字符串形式条件，然后转换处理为DAO层面识别的SQL条件
 * 页面表单元素示例：
 * <p>
 * <ul>
 * <li>search['CN_a_OR_b']</li>
 * <li>search['EQ_id']</li>
 * <li>search['CN_user.name']</li>
 * </ul>
 * <p>
 * Form传递表单参数规则：
 * 1、第一部分：以"search[]"作为查询参数标识
 * 2、第二部分：查询类型
 * 3、第三部分：id_OR_email, category, state, user.userprofile为属性名称，一般对应于Hibernate Entity对应属性，可以以_OR_分割多个属性进行OR查询
 * <p>
 * 上述拼装字符串形式主要用于JSP页面form表单元素name属性值，如果是Java代码层面追加过滤条件，一般直接使用构造函数：
 * PropertyFilter(final MatchType matchType, fianl String propertyName, final Object matchValue)
 * Date: 2015/11/27
 * Time: 10:54
 *
 * @author Rascal
 */
public class PropertyFilter {

    private static final Logger logger = LoggerFactory.getLogger(PropertyFilter.class);

    /*多个属性间OR关系的分割符.*/
    public static final String OR_SEPARATOR = "_OR_";

    /*属性匹配比较类型.*/
    public enum MatchType {
        /*"name":"bk", "description": "is blank", "operator":"IS NULL OR ==''"*/
        BK,

        /*"name":"nb", "description": "is not blank", "operator":"IS NOT NULL AND !=''"*/
        NB,

        /*"name":"nu", "description": "is null", "operator":"IS NULL"*/
        NU,

        /*"name":"nn", "description": "is not null", "operator":"IS NOT NULL"*/
        NN,

        /*"name":"bk", "description": "is blank", "operator":"IS NULL AND !=''"*/
        IN,

        /*"name":"ni", "description": "not in", "operator":"NOT IN"*/
        NI,

        /*"name":"ne", "description": "not equal", "operator":"<>"*/
        NE,

        /*"name":"eq", "description": "equal", "operator":"="*/
        EQ,

        /*"name":"cn", "description": "contains", "operator":"LIKE %abc%"*/
        CN,

        /*"name":"nc", "description": "does not contain", "operator":"NOT LIKE %abc%"*/
        NC,

        /*"name":"bw", "description": "begins with", "operator":"LIKE abc%"*/
        BW,

        /*"name":"bn", "description": "does not begin with", "operator":"NOT LIKE abc%"*/
        BN,

        /*"name":"ew", "description": "ends with", "operator":"LIKE %abc"*/
        EW,

        /*"name":"en", "description": "does not ends with", "operator":"NOT LIKE %abc"*/
        EN,

        /*"name":"bt", "description": "between", "operator":"BETWEEN 1 AND 2"*/
        BT,

        /*"name":"lt", "description": "less", "operator":"<"*/
        LT,

        /*"name":"gt", "description": "greater", "operator":">"*/
        GT,

        /*"name":"le", "description": "less or equal", "operator":"<="*/
        LE,

        /*"name":"ge", "description": "greater or equal", "operator":">="*/
        GE,

        /*@see javax.persistence.criteria.Fetch"*/
        FETCH,

        /*Property Less Equal: <=*/
        PLE,

        /*Property Less Than: <*/
        PLT,

        ACLPREFIXS;
    }

    /*匹配类型*/
    private MatchType matchType = null;

    /*匹配值*/
    private Object matchValue = null;

    /**
     * 匹配属性说明
     * 限制说明：如果是多个属性则取第一个，因此需要确保_OR_定义多个属性属于同一类型
     */
    private Class propertyClass = null;

    /**
     * 属性名称数组，一般是单个属性，如果有_OR_则为多个
     */
    private String[] propertyNames = null;

    /**
     * 集合类型子查询，如查询包含某个商品的所有订单列表，如order上面有个List集合productions对象，则可以类似这样:search['EQ_products.code']
     * 限制说明：框架只支持当前主对象直接定义的集合对象集合查询，不支持再多层嵌套
     */
    private Class subQueryCollectionPropertyType;

    public PropertyFilter() {
    }

    public PropertyFilter(Class<?> entityClass, String filterName, String... values) {
        String matchTypeCode = StringUtils.substringBefore(filterName, "_");

        try {
            matchType = Enum.valueOf(MatchType.class, matchTypeCode);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("filter名称" + filterName + "没有按规则编写，无法得到属性比较类型.", e);
        }

        String propertyNameStr = StringUtils.substringAfter(filterName, "_");
        Assert.isTrue(StringUtils.isNotBlank(propertyNameStr), "filter名称" + filterName + "没有按规则编写，无法得到属性名称,");
        propertyNames = StringUtils.splitByWholeSeparator(propertyNameStr, PropertyFilter.OR_SEPARATOR);
        try {
            if (propertyNameStr.indexOf("count(") > -1) {
                propertyClass = Integer.class;
            } else if (propertyNameStr.indexOf("(") > -1) {
                propertyClass = BigDecimal.class;
            } else {
                Method method = null;
                String[] namesSplits = StringUtils.split(propertyNames[0], ".");
                if (namesSplits.length == 1) {
                    method = MethodUtils.getAccessibleMethod(entityClass, "get" + StringUtils.capitalize(propertyNames[0]));
                } else {
                    Class<?> retClass = entityClass;
                    for (String nameSplit : namesSplits) {
                        method = MethodUtils.getAccessibleMethod(retClass, "get" + StringUtils.capitalize(nameSplit));
                        retClass = method.getReturnType();
                        if (Collection.class.isAssignableFrom(retClass)) {
                            Type genericReturnType = method.getGenericReturnType();
                            if (genericReturnType instanceof ParameterizedType) {
                                retClass = (Class<?>) ((ParameterizedType) genericReturnType).getActualTypeArguments()[0];
                                subQueryCollectionPropertyType = retClass;
                            }
                        }
                    }
                }
                propertyClass = method.getReturnType();
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("无效对象属性定义: " + entityClass + ":" + propertyNames[0], e);
        }

        if (values.length == 1) {
            if (matchType.equals(MatchType.IN) || matchType.equals(MatchType.NI)) {
                String value = values[0];
                values = value.split(",");
            } else if (propertyClass.equals(Date.class) || propertyClass.equals(DateTime.class)) {
                String value = values[0].trim();
                value = value.replace("～", "~");
                if (value.contains(" ")) {
                    values = StringUtils.split(value, "~");
                    if (matchType.equals(MatchType.BT)) {
                        values[0] = values[0].trim();
                        values[1] = values[1].trim();
                    } else {
                        values = new String[]{values[0].trim()};
                    }
                }
            }
        }

        if (values.length == 1) {
            this.matchValue = parseMatchValueByClassType(propertyClass, values[0]);
        } else {
            Object[] matchValues = new Object[values.length];
            for (int i = 0; i < values.length; i++) {
                matchValues[i] = parseMatchValueByClassType(propertyClass, values[0]);
            }
            this.matchValue = matchValues;
        }
    }

    private Object parseMatchValueByClassType(Class propertyClass, String value) {
        if ("NULL".equalsIgnoreCase(value)) {
            return value;
        }
        if (Enum.class.isAssignableFrom(propertyClass)) {
            return Enum.valueOf(propertyClass, value);
        } else if (propertyClass.equals(Boolean.class) || matchType.equals(MatchType.NN) || matchType.equals(MatchType.NU)) {
            return new Boolean(BooleanUtils.toBoolean(value));
        } else if (propertyClass.equals(Date.class)) {
            return DateUtils.parseMultiFormatDate((String) value);
        } else {
            return ConvertUtils.convertStringToObject(value, propertyClass);
        }
    }

    /**
     * Java程序层直接构造过滤器对象，如filters.add(new PropertyFilter(MatchType.EQ, "code", code));
     */
    public PropertyFilter(final MatchType matchType, final String propertyName, final Object matchValue) {
        this.matchType = matchType;
        this.propertyNames = StringUtils.splitByWholeSeparator(propertyName, PropertyFilter.OR_SEPARATOR);
        this.matchType = matchType;
    }

    /**
     * Java程序层直接构造过滤对象，如filters.add(new PropertyFilter(MatchType.LIKE, new String[]{"code", "name"}, value));
     */
    public PropertyFilter(final MatchType matchType, final String[] propertyNames, final Object matchValue) {
        this.matchType = matchType;
        this.propertyNames = propertyNames;
        this.matchValue = matchValue;
    }

    /**
     * 从HttpRequst中创建PropertyFilter列表
     * PropertyFilter命名规则为Filter属性前缀_比较类型属性类型_属性名.
     */
    public static List<PropertyFilter> buildFiltersFromHttpRequest(Class<?> entityClass, ServletRequest request) {
        List<PropertyFilter> filterList = new ArrayList<>();

        //从request中获取含有属性前缀名的参数，构造取出前缀名后的参数Map.
        Map<String, String[]> filterParamMap = getParametersStartingWith(request, "search['", "']");

        //分析参数Map，构造PropertyFilter列表
        for (Map.Entry<String, String[]> entry : filterParamMap.entrySet()) {
            String filterName = entry.getKey();
            String[] values = entry.getValue();
            if (values == null || values.length == 0) {
                continue;
            }

            if (values.length == 1) {
                String value = values[0];
                //如果value值为空，则忽略此filter.
                if (StringUtils.isBlank(value)) {
                    PropertyFilter filter = new PropertyFilter(entityClass, filterName, values);
                    filterList.add(filter);
                }
            } else {
                String[] valueArr = values;
                //如果value值为空，则忽略此filter.
                if (valueArr.length > 0) {
                    Set<String> valueSet = new HashSet<>();
                    Collections.addAll(valueSet, valueArr);
                    if (valueSet.size() > 0) {
                        String[] realValues = new String[valueSet.size()];
                        int cnt = 0;
                        for (String v : valueSet) {
                            realValues[cnt++] = v;
                        }
                        PropertyFilter filter = new PropertyFilter(entityClass, filterName, realValues);
                        filterList.add(filter);
                    }
                }
            }
        }
        return filterList;
    }

    /**
     * 从request对象中提取组装分页和排序对象，参数列表：
     * rows 每页记录数， 默认20
     * page 当前页面数，从1开始，默认为1
     * start 起始记录顺序号，从1开始，用于一些需要精确控制从start到start+size的场景，可选参数，来提供则取值等于page*size
     * sidx 排序属性名称
     * sord 排序规则，asc=升序， desc=降序， 默认asc
     */
    public static Pageable buildPageableFromHttpRequest(HttpServletRequest request) {
        return buildPageableFromHttpRequest(request, null);
    }

    /**
     * 从request对象中提取组装分页和排序对象，参数列表：
     * rows 每页记录数， 默认20
     * page 当前页面数，从1开始，默认为1
     * start 起始记录顺序号，从1开始，用于一些需要精确控制从start到start+size的场景，可选参数，来提供则取值等于page*size
     * sidx 排序属性名称
     * sord 排序规则，asc=升序， desc=降序， 默认asc
     *
     * @param sort 如果传入参数为null， 则从request构建，否则直接取输入sort参数
     */
    public static Pageable buildPageableFromHttpRequest(HttpServletRequest request, Sort sort) {
        String strRows = StringUtils.isBlank(request.getParameter("rows")) ? "20" : request.getParameter("rows");
        if (Integer.valueOf(strRows) < 0) {
            return null;
        }

        int rows = Integer.valueOf(strRows);
        int offset = -1;
        int page = 1;
        String strStart = request.getParameter("start");
        if (StringUtils.isNotBlank(strStart)) {
            offset = Integer.valueOf(strStart) - 1;
            page = (offset + 1) / rows;
            if (page <= 0) {
                page = 1;
            }
        } else {
            String strPage = request.getParameter("page");
            if (StringUtils.isNotBlank(strPage)) {
                page = Integer.valueOf(strPage);
            }
        }

        if (sort == null) {
            sort = buildSortFromHttpRequest(request);
        }
        return new ExtPageRequest(page - 1, rows, sort, offset);
    }

    /**
     * 从request对象中提取组装排序对象，参数列表：
     * sidx 排序属性名称
     * sord 排序规则， asc=升序， desc=降序， 默认asc
     */
    public static Sort buildSortFromHttpRequest(HttpServletRequest request) {
        String sidx = StringUtils.isBlank(request.getParameter("sidx")) ? "id" : request.getParameter("sidx");
        Sort.Direction sord = "desc".equalsIgnoreCase(request.getParameter("sord")) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = null;
        //按照逗号切分支持多属性排序
        for (String sidxItem : sidx.split(",")) {
            if (StringUtils.isNotBlank(sidxItem)) {
                //再按空格切分获取排序属性和排序方向
                String[] sidxItemWithOrder = sidxItem.trim().split(" ");
                String sortname = sidxItemWithOrder[0];
                //如果查询属性包含_OR_则取第一个作为排序属性
                //因此在写OR多属性查询时注意把排序属性写在最前面
                if (sortname.indexOf(OR_SEPARATOR) > -1) {
                    sortname = StringUtils.substringBefore(sortname, OR_SEPARATOR);
                }
                //如果单个属性没有跟随排序方向，则取Grid组件传入的sord参数定义
                if (sidxItemWithOrder.length == 1) {
                    if (sort == null) {
                        //初始化排序对象
                        sort = new Sort(sord, sortname);
                    } else {
                        //and追加多个排序
                        sort = sort.and(new Sort(sord, sortname));
                    }

                } else {
                    //排序属性后面空格跟随排序方向定义
                    String sortorder = sidxItemWithOrder[1];
                    if (sord == null) {
                        sort = new Sort("desc".equalsIgnoreCase(sortorder) ? Sort.Direction.DESC : Sort.Direction.ASC);
                    } else {
                        sort = sort.and(new Sort("desc".equalsIgnoreCase(sortorder) ? Sort.Direction.DESC : Sort.Direction.ASC, sortname));
                    }
                }
            }
        }
        return sort;
    }

    /**
     * 获取比较值.
     */
    public Object getMatchValue() {
        return matchValue;
    }

    /**
     * 获取比较方式.
     */
    public MatchType getMatchType() {
        return matchType;
    }

    /**
     * 获取比较属性名称列表
     */
    public String[] getPropertyNames() {
        return propertyNames;
    }

    /**
     * 获取唯一的比较属性名称.
     */
    public String getPropertyName() {
        Assert.isTrue(propertyNames.length == 1, "There are not only one property in this filter.");
        return propertyNames[0];
    }

    /**
     * 是否比较多个属性
     */
    public boolean hasMultiProperties() {
        return propertyNames.length > 1;
    }

    /**
     * 构造一个缺省过滤集合.
     */
    public static List<PropertyFilter> buildDefaultFilterList() {
        return new ArrayList<PropertyFilter>();
    }

    public Class getPropertyClass() {
        return propertyClass;
    }

    public Class getSubQueryCollectionPropertyType() {
        return subQueryCollectionPropertyType;
    }

    public static Map<String, String[]> getParametersStartingWith(ServletRequest request, String prefix, String suffix) {
        Assert.notNull(request, "Request must not be null");
        Enumeration paramNames = request.getParameterNames();
        Map<String, String[]> params = new TreeMap<>();
        if (prefix == null) {
            prefix = "";
        }
        if (suffix == null) {
            suffix = "";
        }
        while (paramNames != null && paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();
            if (("".equals(prefix) || paramName.startsWith(prefix)) && ("".equals(suffix) || paramName.endsWith(suffix))) {
                String unprefixed = paramName.substring(prefix.length(), paramName.length() - suffix.length());
                String[] values = request.getParameterValues(paramName);
                if (values.length > 1) {
                    params.put(unprefixed, values);
                } else if (values.length == 1) {
                    params.put(unprefixed, new String[]{values[0]});
                }
            }
        }
        return params;
    }
}
