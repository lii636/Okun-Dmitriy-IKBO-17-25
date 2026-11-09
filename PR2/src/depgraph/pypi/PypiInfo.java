package depgraph.pypi;

import java.util.List;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PypiInfo {
    @SerializedName("requires_dist")
    List<String> requiresDist;
}
