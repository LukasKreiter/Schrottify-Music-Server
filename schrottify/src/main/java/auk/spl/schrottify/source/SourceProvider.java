package auk.spl.schrottify.source;

import java.util.List;

public interface SourceProvider {
    List<Candidate> search(SearchQuery query);
}
