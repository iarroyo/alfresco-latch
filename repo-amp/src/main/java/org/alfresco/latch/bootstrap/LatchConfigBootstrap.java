/**
 * 
 */
package org.alfresco.latch.bootstrap;

import java.util.concurrent.ThreadPoolExecutor;

import org.alfresco.latch.config.LatchConfig;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.extensions.surf.util.AbstractLifecycleBean;

/**
 * @author iarroyo
 *
 * @mail iarroyoescobar@gmail.com
 * 
 * Initialize the latch configuration. The latch configuration is
 * persisted in the database.
 *
 */
public class LatchConfigBootstrap extends AbstractLifecycleBean {

	private TransactionService transactionService;
	private LatchConfig latchConfig;
	private ThreadPoolExecutor threadExecuter;
	
	private static final Log logger = LogFactory.getLog(LatchConfigBootstrap.class);
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.extensions.surf.util.AbstractLifecycleBean#onBootstrap
	 * (org.springframework.context.ApplicationEvent)
	 */
	@Override
	protected void onBootstrap(ApplicationEvent arg0) {
		
		threadExecuter.execute(new LatchConfigThread());
	}
	
	private class LatchConfigThread implements Runnable
	{

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
            AuthenticationUtil.runAs(new RunAsWork<Object>()
            {
                public Object doWork()
                {
                    RetryingTransactionCallback<Void> callback = new RetryingTransactionCallback<Void>()
                    {
                        public Void execute() throws Throwable
                        {
                            logger.info("Initializing latch configuration.");
                            latchConfig.init();
                            logger.info("Latch configuration initialized correctly.");

                            return null;
                        }
                    };
                    transactionService.getRetryingTransactionHelper().doInTransaction(callback);

                    return null;
                }
            }, AuthenticationUtil.getSystemUserName());
        }
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.extensions.surf.util.AbstractLifecycleBean#onShutdown
	 * (org.springframework.context.ApplicationEvent)
	 */
	@Override
	protected void onShutdown(ApplicationEvent arg0) {
		// NOOP
	}
	
	
    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }

    public void setLatchConfig(LatchConfig latchConfig)
    {
        this.latchConfig = latchConfig;
    }
    
    /**
     * Set the thread executer.
     * 
     * @param threadExecuter Thread executer
     */
    public void setThreadExecuter(final ThreadPoolExecutor threadExecuter)
    {
        this.threadExecuter = threadExecuter;
    }

}
