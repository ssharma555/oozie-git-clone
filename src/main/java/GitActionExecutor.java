import java.io.File;

import org.apache.oozie.ErrorCode;
import org.apache.oozie.action.ActionExecutor;
import org.apache.oozie.action.ActionExecutorException;
import org.apache.oozie.action.ActionExecutorException.ErrorType;
import org.apache.oozie.client.WorkflowAction;
import org.apache.oozie.util.XmlUtils;
import org.eclipse.jgit.api.Git;
import org.jdom.Element;
import org.jdom.Namespace;

public class GitActionExecutor extends ActionExecutor {

	private static final String NODENAME = "git";

	private static final String SUCCEEDED = "OK";
	private static final String FAILED = "FAIL";
	private static final String KILLED = "KILLED";

	public GitActionExecutor() {
		super(NODENAME);
	}

	@Override
	public void check(Context context, WorkflowAction action)
			throws ActionExecutorException {

		// Should not be called for synch operation
		throw new UnsupportedOperationException();
	}

	@Override
	public void end(Context context, WorkflowAction action)
			throws ActionExecutorException {

		String externalStatus = action.getExternalStatus();
		WorkflowAction.Status status = externalStatus.equals(SUCCEEDED) ? WorkflowAction.Status.OK
				: WorkflowAction.Status.ERROR;
		context.setEndData(status, getActionSignal(status));
	}

	@Override
	public boolean isCompleted(String arg0) {

		return true;
	}

	@Override
	public void kill(Context context, WorkflowAction action)
			throws ActionExecutorException {

		context.setExternalStatus(KILLED);
		context.setExecutionData(KILLED, null);
	}

	@Override
	public void start(Context context, WorkflowAction action)
			throws ActionExecutorException {

		// Get parameters from Node configuration
		try {
			Element actionXml = XmlUtils.parseXml(action.getConf());
			Namespace ns = Namespace
					.getNamespace("uri:custom:git-action:0.1");

			String repository = actionXml.getChildTextTrim("repository", ns);
			File filePath = new File(actionXml.getChildTextTrim("hdfsPath", ns));
			cloneRepo(repository, filePath);
			context.setExecutionData(SUCCEEDED, null);
		} catch (Exception e) {
			context.setExecutionData(FAILED, null);
			throw new ActionExecutorException(ErrorType.FAILED,
					ErrorCode.E0000.toString(), e.getMessage());
		}
	}

	// Sending an email
	public void cloneRepo(String repository, File filePath) throws Exception {

		Git.cloneRepository()
                .setURI(repository)
                .setDirectory(filePath)
                .call();
	}
}