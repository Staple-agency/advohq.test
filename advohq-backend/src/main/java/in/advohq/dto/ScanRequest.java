package in.advohq.dto;

public record ScanRequest(
    String deviceName,
    int resolution,      // 150, 300, 600 dpi
    String colorMode     // "bw" (black & white), "gray" (grayscale), "color"
) {}
