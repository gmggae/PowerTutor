package com.henny.PowerTutor2.components;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;

public class Logging extends Thread {
	private final String TAG = "Logging";

	private File writeFile;

	protected long beginTime;
	protected long iterationInterval;
	private Context context;
	FileWriter fr = null;
	BufferedWriter outbr = null;

	public Logging(Context context) {
		this.context = context;

		File writeDirectory = new File(Environment
				.getExternalStorageDirectory().getAbsolutePath()
				+ "/PowerTraceWithLog");
		if (!writeDirectory.exists()) {
			writeDirectory.mkdir();
		}

		writeFile = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/PowerTraceWithLog", "Log"
				+ System.currentTimeMillis() + ".log");
	}

	/*
	 * This is called once at the begginning of the daemon loop.
	 */
	public void init(long beginTime, long iterationInterval) {
		this.beginTime = beginTime;
		this.iterationInterval = iterationInterval;
		try {
			fr = new FileWriter(writeFile, true);
			outbr = new BufferedWriter(fr);
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
					new String[] { "logcat", "-v", "process" });
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
				outbr.write(iter + " " + line);
				outbr.newLine();

				// Runtime.getRuntime().exec(new String[] { "logcat", "-c" });
				/*try {
					sleep(beginTime + iter * iterationInterval - curTime);
				} catch (InterruptedException e) {
					break;
				}*/
			}

			try {
				outbr.close();
				fr.close();
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
