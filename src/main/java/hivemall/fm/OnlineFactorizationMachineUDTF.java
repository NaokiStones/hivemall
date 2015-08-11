package hivemall.fm;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;

import hivemall.UDTFWithOptions;

public abstract class OnlineFactorizationMachineUDTF extends UDTFWithOptions{
	
	// Option variables
	/** The number of latent factors*/
	protected int factor;
	/** The regularization factor */
	protected float lambda;
	/** Whether to use regression or classification */
	protected String task;
	/** Standard derivation to initialize matrices */
	protected float sigma;
	/** How to initialize matrices*/
	protected String init;
	/** conversionCheck */
	protected boolean conversionCheck;
	/** The number of iteration*/
	protected int iterations;
	
	public OnlineFactorizationMachineUDTF(){
		this.factor = 10;
		this.lambda = 0.03f;
		this.task = "regression";
		this.sigma = 1.0f;
		this.init = "random";
		this.conversionCheck = true;
		this.iterations = 1;
		
		
	}


	@Override
	protected Options getOptions() {
		Options opts = new Options();
		opts.addOption("k", "factor", true , "The number of latent factor[default:10]");
		opts.addOption("r", "lambda", true, "The regularization factor[default:0.03]");
		opts.addOption("task", true, "Regression or Classification[default:regression]");
		opts.addOption("s", "sigma", true, "Standard Variation[default: 1.0]");
		opts.addOption("init", true, "How to initialize matrices[default: random]");
		opts.addOption("disable_cv", false, "Whether to disable convergence check[default:enabled]");
		opts.addOption("i", "iter", true, "The number of iteration[default: 1]");
		
		
		return opts;
	}

	@Override
	protected CommandLine processOptions(ObjectInspector[] argOIs) throws UDFArgumentException {
		CommandLine cl = null;
		
		
		
		if(argOIs.length>=4){
		}

		return null;
	}

	@Override
	public void close() throws HiveException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public StructObjectInspector initialize(ObjectInspector[] arg0) throws UDFArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void process(Object[] arg0) throws HiveException {
		// TODO Auto-generated method stub
		
	}


}
