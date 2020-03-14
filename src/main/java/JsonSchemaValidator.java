import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * json-schema-validator 테스트
 *
 * @author madplay
 * created on 2020. 3. 14.
 */
public class JsonSchemaValidator {
    final ObjectMapper mapper = new ObjectMapper();

    private HashMap<String, Object> getTestData() {
        List<Content> contents = List.of(
                new Content("1", "제목1", "text"),
                new Content("2", "제목2", "text"),
                new Content("3", "제목3", "text")
        );

        return new HashMap<>() {
            {
                put("contents", contents);
            }
        };
    }

    private JsonNode convertObjToJsonNode(Object object) {
        try {
            return mapper.valueToTree(object);
        } catch (Exception e) {
            //
        }
        return null;
    }

    private JsonNode convertCardRuleToJsonNode(String cardRule) {
        try {
            return mapper.readTree(cardRule);
        } catch (Exception e) {
            //
        }
        return null;
    }

    private String getJsonSchemaFromFile() {
        try {
            Path path = Paths.get(ClassLoader.getSystemResource("sample_schema.json").toURI());
            return Files.readAllLines(path).stream().collect(Collectors.joining());
        } catch (IOException | URISyntaxException e) {
            //
        }
        return "";
    }

    public void validate() {
        // 문자열 스키마를 기반으로 JsonNode 생성
        JsonNode schemaNode = convertCardRuleToJsonNode(getJsonSchemaFromFile());

        // 스키마 validator 초기화
        JsonSchemaFactory factory = JsonSchemaFactory.byDefault();

        ProcessingReport report = null;
        try {

            // 스키마 객체 생성
            JsonSchema schema = factory.getJsonSchema(schemaNode);

            // 테스트 데이터를 JsonNode 타입으로 변환한다.
            JsonNode data = convertObjToJsonNode(getTestData());

            // 검증
            report = schema.validate(data);

        } catch (Exception e) {
            //
        }

        if (report.isSuccess()) {
            System.out.println("Json data is valid :)");
        } else {
            System.out.println("Json data is not valid :( => " + report);
        }
    }

    public static void main(String[] args) {
        JsonSchemaValidator tester = new JsonSchemaValidator();
        tester.validate();
    }
}