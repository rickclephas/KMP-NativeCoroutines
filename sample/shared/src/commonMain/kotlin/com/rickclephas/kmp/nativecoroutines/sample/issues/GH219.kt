import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow

public interface IGH219 {
    @NativeCoroutinesState
    public val uiState: StateFlow<String>
}

public class GH219: IGH219 {
    override val uiState: StateFlow<String> = MutableStateFlow("OK")
}
