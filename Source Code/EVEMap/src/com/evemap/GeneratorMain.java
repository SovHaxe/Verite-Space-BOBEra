package com.evemap;

/*
 * Copyright (c) 2007, [AEGL, UNL] Paladin Vent
 * All rights reserved.
 *
 * Additional authors:
 *    [CE] Mirida (Multithreading)
 *    [CORE., AUS C] Verite Rendition (Various, General Upkeep: Aug 2007 to Present)
 *    [MOP] Calistra "Draekas" Darkwater (Multiple-Instance Name placement)
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the United Legion nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY [AEGL, UNL] Paladin Vent ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL [AEGL, UNL] Paladin Vent BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GeneratorMain {

	private static String url = "jdbc:mysql://localhost/eve";
	private static String root = "root";
	private static String pw = "";
	private static boolean isBatch = false;
	
	/**
	 * Valid arguments are as follows:
	 * [-url : The URL of the server.]
	 * [-user : The username for accessing the server]
	 * [-password : The password for accessing the server]
	 * [-isBatch : [t f true false] Defaults to false, when true no UI shows]
	 * [-help : Display the set of acceptable commands]
	 * 
	 * @param args String[]
	 */
	public static void main(String[] args) {		
		String dateString = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
		parseVariables(args);

		Connection[] db;
		
		try{
//			Initialize SQL connections, One for each thread. Connection is not
//			thread safe.
			DriverManager.registerDriver(new com.mysql.jdbc.Driver());
			db = new Connection[MapConstants.THREADPOOL_SIZE + 1];
			for(int con = 0; con < MapConstants.THREADPOOL_SIZE + 1; con++)
				db[con] = DriverManager.getConnection(url, root, pw);

			DataManager data = new DataManager(db, dateString);
			
			if(!isBatch)
			new StarMapGeneratorFrame(data);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	private static void parseVariables(String[] args) {
		for(int i = 0; i < args.length; i++){
			String arg = args[i];
			if(arg.equalsIgnoreCase("-url")){
				url = args[++i];
			} else if(arg.equalsIgnoreCase("-user")){
				root = args[++i];
			} else if(arg.equalsIgnoreCase("-password")){
				pw = args[++i];
			} else if(arg.equalsIgnoreCase("-isBatch")){
				String inp = args[++i];
				isBatch = inp.equalsIgnoreCase("t") || inp.equalsIgnoreCase("true");
			} else if(arg.equalsIgnoreCase("-help")){
				System.out.println("Valid Arguments:");
				System.out.println("[-url : The URL of the server.]");
				System.out.println("[-user : The username for accessing the server]");
				System.out.println("[-password : The password for accessing the server]");
				System.out.println("[-isBatch : [t f true false] Defaults to false, when true no UI shows]");
				System.out.println("[-help : Display the set of acceptable commands]");
			} else {
				System.out.println("Cannot parse " + arg + ", Unknown command.");
			}
		}
	}

}
