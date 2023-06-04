package com.teasgen.keyraces.server;

import java.io.Serializable;

public record ClientStats(int totalNumber, int errorsNumber) implements Serializable {
}