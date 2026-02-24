package com.ecommerce.infrastructure.adapter.persistence.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

/**
 * ADAPTATEUR PERSISTENCE - Document MongoDB.
 */
@Document(collection = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMongoDocument {

    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    @Indexed(unique = true)
    private String email;

    private String password;
    private Set<String> roles;
    private boolean active;
}
