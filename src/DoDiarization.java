import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import system.Diarization;
public class DoDiarization {
	private String fInputMask;
	private String sInputMask;
	private String sOutputMask;
	private String sampleRate = "8";
	private String logFile;
	public DoDiarization(){
		File loggerFile = new File("DoDiarization.log");
		try {
			System.setOut(new PrintStream(new FileOutputStream(loggerFile.getAbsolutePath(), true)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	public int run(String fInputMask, String sInputMask, String logFile, int sampleRate){
		this.fInputMask = fInputMask;
		this.sInputMask = sInputMask;
		this.logFile = logFile;
		this.sampleRate = String.valueOf(sampleRate);
		System.out.println("\t enter run()\nfInputMask:"+fInputMask+" sInputMask:"+sInputMask);
		System.out.flush();
		System.out.println("fInputMask: "+getfInputMask());
		System.out.flush();
		if(getfInputMask() == null || getfInputMask().trim().equals("")){
			return -1;
		}
		System.out.flush();
		System.out.println("sInputMask: "+getsInputMask());
		System.out.flush();
		if(getsInputMask() == null || getsInputMask().trim().equals("")){
			this.sInputMask = fInputMask+".sns";
		}
		System.out.println("sOutputMask: "+getsOutputMask());
		System.out.flush();
		if(getsOutputMask() == null || getsOutputMask().trim().equals("")){
			this.sOutputMask = fInputMask+".seg";
		}
		String[] args ={"--fInputMask="+this.fInputMask, "--sInputMask="+this.sInputMask, "--sOutputMask="+this.sOutputMask,
				"--SampleRate="+this.sampleRate, "--OutLogFile="+this.logFile};
		for(String eachArg : args){
			System.out.println(eachArg);
		}
		System.out.println(this.getClass()+"\t run()");
		System.out.flush();
		return system.Diarization.make(args);
	}
	
	public String getfInputMask() {
		return fInputMask;
	}

	public void setfInputMask(String fInputMask) {
		System.out.println(this.getClass()+"\t setfInputMask()");
		this.fInputMask = fInputMask;
	}

	public String getsInputMask() {
		return sInputMask;
	}

	public void setsInputMask(String sInputMask) {
		System.out.println(this.getClass()+"\t setsInputMask()"+sInputMask);
		this.sInputMask = sInputMask;
	}

	public String getsOutputMask() {
		return sOutputMask;
	}

	public void setsOututMask(String sOutputMask) {
		System.out.println(this.getClass()+"\t setsOututMask()"+sOutputMask);
		this.sOutputMask = sOutputMask;
	}

	public String getSampleRate() {
		return sampleRate;
	}

	public void setSampleRate(String sampleRate) {
		this.sampleRate = sampleRate;
	}
	
	public String getLogFile() {
		return logFile;
	}

	public void setLogFile(String logFile) {
		this.logFile = logFile;
	}
}
