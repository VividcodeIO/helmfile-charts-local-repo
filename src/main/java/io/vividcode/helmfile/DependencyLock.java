package io.vividcode.helmfile;

import java.util.List;
import lombok.Data;

@Data
public class DependencyLock {

    List<Dependency> dependencies;
    String version;
    String digest;
    String generated;
}
