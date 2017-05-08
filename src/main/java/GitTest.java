import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;


public class GitTest {

	public static void main(String[] args) throws InvalidRemoteException, TransportException, GitAPIException {
		// TODO Auto-generated method stub
		File file = new File("/Users/ssharma/Documents/");
		Git.cloneRepository()
        .setURI("https://github.com/apache/ambari.git")
        .setDirectory(file)
        .call();
	}

}
