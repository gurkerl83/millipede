package ch.cyberduck.handler;

import ch.cyberduck.core.Host;
import java.util.List;

public interface ResultHandler {

  void handleResult(ResultContext context, List<Host> h);

}
