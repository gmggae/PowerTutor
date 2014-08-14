package com.henny.PowerTutor2.components;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.zip.DeflaterOutputStream;

import android.content.Context;
import android.os.SystemClock;

public class Logging extends Thread {
	private final String TAG = "Logging";

	private String writeFile;

	protected long beginTime;
	protected long iterationInterval;
	private Context context;
	OutputStreamWriter outbr = null;
	DeflaterOutputStream deflateStream;

	public Logging(Context context) {
		this.context = context;

		writeFile = context.getFileStreamPath("LogTrace.log")
				.getAbsolutePath();
	}

	/*
	 * This is called once at the begginning of the daemon loop.
	 */
	public void init(long beginTime, long iterationInterval) {
		this.beginTime = beginTime;
		this.iterationInterval = iterationInterval;
		try {
			
			deflateStream = new DeflaterOutputStream(new FileOutputStream(
					writeFile));
			
			outbr = new OutputStreamWriter(deflateStream);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	public void run() {
		android.os.Process
				.setThreadPriority(android.os.Process.THREAD_PRIORITY_MORE_FAVORABLE);

		try {
			Runtime.getRuntime().exec(new String[] { "logcat", "-c" });

			Process logcat = Runtime.getRuntime().exec(
					new String[] { "logcat", "-v", "brief" });
			BufferedReader br = new BufferedReader(new InputStreamReader(
					logcat.getInputStream()), 4 * 1024);
			String line;
			for (long iter = 0; !Thread.interrupted()
					&& ((line = br.readLine()) != null);) {

				if (interrupted()) {
					break;
				}

				long curTime = SystemClock.elapsedRealtime();
				iter = (long) (1 + (curTime - beginTime) / iterationInterval);
				outbr.write(iter + "@#" + line + "\n");				
			}

			try {
				outbr.close();
				deflateStream.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

}
