package com.myself.everythingP.core.common;

import lombok.Data;

import java.util.Set;

@Data
public class HandlePath {
    Set<String> includePath;
    Set<String> excludePath;
}
