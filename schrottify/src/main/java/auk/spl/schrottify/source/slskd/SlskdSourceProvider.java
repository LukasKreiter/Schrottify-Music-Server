package auk.spl.schrottify.source.slskd;

import auk.spl.schrottify.source.Candidate;
import auk.spl.schrottify.source.SearchQuery;
import auk.spl.schrottify.source.SourceProvider;
import java.util.List;

public class SlskdSourceProvider implements SourceProvider {
    private final SlskdClient client;

    public SlskdSourceProvider(SlskdClient client) {
        this.client = client;
    }

    @Override
    public List<Candidate> search(SearchQuery query) {
        return client.search(query);
    }
}
