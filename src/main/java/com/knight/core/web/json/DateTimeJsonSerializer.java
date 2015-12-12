package com.knight.core.web.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.knight.core.util.DateUtils;

import java.io.IOException;
import java.util.Date;

/**
 * Date: 2015/11/18
 * Time: 19:12
 *
 * @author Rascal
 */
public class DateTimeJsonSerializer extends JsonSerializer<Date> {

    @Override
    public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        if (date != null) {
            jsonGenerator.writeString(DateUtils.formatTime(date));
        }
    }

    public Class<Date> handledType() {
        return Date.class;
    }
}
