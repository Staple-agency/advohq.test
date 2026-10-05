package in.advohq.dto;

/** One signed URL the browser PUTs a single part to. */
public record DirectUploadPartUrl(int partNumber, String url) {}
