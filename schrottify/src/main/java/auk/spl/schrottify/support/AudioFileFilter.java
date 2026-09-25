package auk.spl.schrottify.support;

import java.nio.file.Path;
import java.util.Set;

/** Filters files to include only audio files. */
public final class AudioFileFilter {

    private static final Set<String> AUDIO_EXTENSIONS = Set.of(
            ".mp3", ".flac", ".ogg", ".wav", ".aac", ".m4a", ".wma");

    private AudioFileFilter() {
    }

    /**
     * Checks if the given file path has an audio file extension.
     * The check is case-insensitive.
     * 
     * @param path The file path to check.
     * @return true if the file has an audio extension, false otherwise.
     */
    public static boolean isAudioFile(Path path) {
        if (path == null) {
            return false;
        }
        String fileName = path.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == fileName.length() - 1) {
            return false;
        }
        String extension = fileName.substring(dotIndex).toLowerCase();
        return AUDIO_EXTENSIONS.contains(extension);
    }
}