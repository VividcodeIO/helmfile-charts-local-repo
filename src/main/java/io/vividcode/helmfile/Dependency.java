package io.vividcode.helmfile;

import lombok.Data;

@Data
public class Dependency {

    String name;
    String repository;
    String version;
}
