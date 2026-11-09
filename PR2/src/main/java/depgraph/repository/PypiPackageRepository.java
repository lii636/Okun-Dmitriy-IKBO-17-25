package depgraph.repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import depgraph.exception.DepgraphException;
import depgraph.model.Dependency;
import depgraph.pypi.PypiInfo;
import depgraph.pypi.PypiVersionResponse;
import depgraph.pypi.RequirementParser;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PypiPackageRepository implements PackageRepository {
    private final String repo;
    private final HttpTransport transport;
    private final Gson gson;

    public PypiPackageRepository(String repo, HttpTransport transport) {
        this(repo, transport, new Gson());
    }

    @Override
    public List<Dependency> directDependencies(String packageName, String version) {
        String body = transport.get(PypiUrl.jsonMetadata(repo, packageName, version));
        PypiVersionResponse response = parse(body);
        PypiInfo info = response.info();
        if (info == null || info.requiresDist() == null) {
            return List.of();
        }
        Set<String> names = new LinkedHashSet<>();
        for (String requirement : info.requiresDist()) {
            if (requirement == null || RequirementParser.hasExtraMarker(requirement)) {
                continue;
            }
            RequirementParser.packageName(requirement).ifPresent(names::add);
        }
        List<String> sorted = new ArrayList<>(names);
        sorted.sort(Comparator.naturalOrder());
        List<Dependency> dependencies = new ArrayList<>(sorted.size());
        for (String name : sorted) {
            dependencies.add(new Dependency(name));
        }
        return List.copyOf(dependencies);
    }

    private PypiVersionResponse parse(String body) {
        try {
            PypiVersionResponse response = gson.fromJson(body, PypiVersionResponse.class);
            if (response == null) {
                throw new DepgraphException("invalid repository response");
            }
            return response;
        } catch (JsonSyntaxException exception) {
            throw new DepgraphException("invalid repository response", exception);
        }
    }
}
