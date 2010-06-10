/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.millipede.states;

/**
 *
 * @author gurkerl
 */
public class JobStates {

    public enum JobState {
    Initializing, Start, Pause, Stop, Ready 
    }

}

//package states
//{
//	import types.Enum;
//
//	public final class JobState extends Enum
//	{
//		{initEnum(JobState);} // static ctor
//
//		public static const Initializing	:JobState = new JobState();
//		public static const Start			:JobState = new JobState();
//		public static const Pause			:JobState = new JobState();
//		public static const Stop			:JobState = new JobState();
//		public static const Ready			:JobState = new JobState();
//
//	}
//}
