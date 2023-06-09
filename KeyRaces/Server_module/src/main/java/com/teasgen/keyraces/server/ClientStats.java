package com.teasgen.keyraces.server;

import java.io.Serializable;

/**
 * Handle client stats
 * @param totalNumber total number of correctly written letters
 * @param errorsNumber number of errors
 * @param time remain time
 */
public record ClientStats(int totalNumber, int errorsNumber, int time) implements Serializable {
}