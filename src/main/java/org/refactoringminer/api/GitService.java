package org.refactoringminer.api;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import java.util.Map;
import java.util.Set;

/**
 * Simple service to make git related tasks easier.  
 *
 */
public interface GitService {

	/**
	 * Clone the git repository given by {@code cloneUrl} only if is does not exist yet in {@code folder}.
	 * 
	 * @param folder The folder to store the local repo.
	 * @param cloneUrl The repository URL.
	 * @return The repository object (JGit library).
	 * @throws Exception propagated from JGit library.
	 */
	Repository cloneIfNotExists(String folder, String cloneUrl/*, String branch*/) throws Exception;

	/**
	 * Clone the git repository given by {@code cloneUrl} only if is does not exist yet in {@code folder}.
	 * 
	 * @param folder The folder to store the local repo.
	 * @param cloneUrl The repository URL.
	 * @param username Username used for private repository access.
	 * @param token Personal Access Token used for private repository access.
	 * @return The repository object (JGit library).
	 * @throws Exception propagated from JGit library.
	 */
	Repository cloneIfNotExists(String folder, String cloneUrl, String username, String token) throws Exception;
	
	Repository openRepository(String folder) throws Exception;

	int countCommits(Repository repository, String branch) throws Exception;

	void checkout(Repository repository, String commitId) throws Exception;

	RevWalk fetchAndCreateNewRevsWalk(Repository repository) throws Exception;

	RevWalk fetchAndCreateNewRevsWalk(Repository repository, String branch) throws Exception;

	RevWalk createAllRevsWalk(Repository repository) throws Exception;

	RevWalk createAllRevsWalk(Repository repository, String branch) throws Exception;

	Iterable<RevCommit> createRevsWalkBetweenTags(Repository repository, String startTag, String endTag) throws Exception;

	Iterable<RevCommit> createRevsWalkBetweenCommits(Repository repository, String startCommitId, String endCommitId) throws Exception;

	void fileTreeDiff(Repository repository, RevCommit currentCommit, Set<String> filesBefore, Set<String> filesCurrent, Map<String, String> renamedFilesHint) throws Exception;

	Churn churn(Repository repository, RevCommit currentCommit) throws Exception;
}
