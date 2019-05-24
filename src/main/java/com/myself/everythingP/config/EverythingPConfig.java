package com.myself.everythingP.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@Getter
@ToString
public final class EverythingPConfig {
    private static volatile EverythingPConfig config;

    private EverythingPConfig() {

    }

    private Set<String> includePath = new HashSet<>();
    private Set<String> excludePath = new HashSet<>();

    @Setter
    private Integer maxReturnThingRecode = 30;

    @Setter
    private Boolean orderByAsc = true;

    public static EverythingPConfig getInstance() {
        if (config == null) {
            synchronized (EverythingPConfig.class) {
                if (config == null) {
                    config = new EverythingPConfig();
                    config.ConfigPath();
                }
            }
        }
        return config;
    }

    private void ConfigPath() {
        FileSystem fileSystem = FileSystems.getDefault();
        Iterable<Path> iterable = fileSystem.getRootDirectories();
        iterable.forEach(new Consumer<Path>() {
            @Override
            public void accept(Path path) {
                config.includePath.add(path.toString());
            }
        });
        String osName = System.getProperty("os.name");
        if (osName.startsWith("Windows")) {
            config.excludePath.add("C:\\Windows");
            config.excludePath.add("C:\\Program Files");
            config.excludePath.add("C:\\Program Files (x86)");
            config.excludePath.add("C:\\ProgramData");
        } else {
            config.excludePath.add("/tmp");
            config.excludePath.add("/etc");
            config.excludePath.add("/root");
        }
    }

//    public static void main(String[] args) {
//        System.out.println(System.getProperty("os.name"));
//    }
}
