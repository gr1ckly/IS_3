package org.example.lab1.model;

import org.example.lab1.entities.dao.Person;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;

import java.time.LocalDateTime;

public class PersonYAMLConstructor extends Constructor {

    public PersonYAMLConstructor(LoaderOptions options) {
        super(Person.class, options);
    }

    @Override
    protected Object constructObject(Node node) {
        if (node.getType() == Float.class && node instanceof ScalarNode scalar) {
            return Float.valueOf(scalar.getValue());
        }
        if (node.getType() == LocalDateTime.class && node instanceof ScalarNode scalar) {
            return LocalDateTime.parse(scalar.getValue());
        }
        return super.constructObject(node);
    }
}

