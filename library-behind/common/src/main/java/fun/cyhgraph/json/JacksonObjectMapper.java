package fun.cyhgraph.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

public class JacksonObjectMapper extends ObjectMapper {

    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT);
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT);
    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT);

    public JacksonObjectMapper() {
        super();
        this.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.getDeserializationConfig().withoutFeatures(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        SimpleModule simpleModule = new SimpleModule()
                .addDeserializer(LocalDateTime.class, new StdDeserializer<>(LocalDateTime.class) {
                    @Override
                    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                        String value = p.getValueAsString();
                        return value == null || value.isBlank() ? null : LocalDateTime.parse(value, DATE_TIME_FORMATTER);
                    }
                })
                .addDeserializer(LocalDate.class, new StdDeserializer<>(LocalDate.class) {
                    @Override
                    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                        String value = p.getValueAsString();
                        return value == null || value.isBlank() ? null : LocalDate.parse(value, DATE_FORMATTER);
                    }
                })
                .addDeserializer(LocalTime.class, new StdDeserializer<>(LocalTime.class) {
                    @Override
                    public LocalTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                        String value = p.getValueAsString();
                        return value == null || value.isBlank() ? null : LocalTime.parse(value, TIME_FORMATTER);
                    }
                })
                .addSerializer(LocalDateTime.class, new StdSerializer<>(LocalDateTime.class) {
                    @Override
                    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider provider) throws IOException {
                        gen.writeString(value.format(DATE_TIME_FORMATTER));
                    }
                })
                .addSerializer(LocalDate.class, new StdSerializer<>(LocalDate.class) {
                    @Override
                    public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider provider) throws IOException {
                        gen.writeString(value.format(DATE_FORMATTER));
                    }
                })
                .addSerializer(LocalTime.class, new StdSerializer<>(LocalTime.class) {
                    @Override
                    public void serialize(LocalTime value, JsonGenerator gen, SerializerProvider provider) throws IOException {
                        gen.writeString(value.format(TIME_FORMATTER));
                    }
                });

        this.registerModule(simpleModule);
    }
}
