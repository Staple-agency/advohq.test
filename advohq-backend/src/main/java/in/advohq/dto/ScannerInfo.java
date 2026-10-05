package in.advohq.dto;

import java.util.List;

public record ScannerInfo(
    String name,              // "Canon LiDE 400" or "hp:libusb:001:002"
    String model,             // Human-readable model name
    boolean available,        // True if scanner is connected and ready
    List<Integer> resolutions, // Supported DPI: [150, 300, 600]
    List<String> colorModes   // "bw", "gray", "color"
) {}
