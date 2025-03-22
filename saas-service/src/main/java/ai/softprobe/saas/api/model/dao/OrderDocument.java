package ai.softprobe.saas.api.model.dao;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "Order")
@FieldNameConstants
public class OrderDocument extends ModelBase {

    @Getter
    @Setter
    public static class Plan {
        private int tierId;
        // 1: front, 2: back-end, 4: full stack
        private int category;
    }
}
