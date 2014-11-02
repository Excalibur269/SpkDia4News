package system;

import java.io.IOException;
import java.io.InputStream;

import lib.MainTools;
import libClusteringData.Cluster;
import libClusteringData.ClusterSet;
import libFeature.AudioFeatureSet;
import libModel.gaussian.GMM;
import libModel.gaussian.GMMArrayList;
import libModel.gaussian.GMMFactory;
import parameter.Parameter;
import programs.MDecode;
import programs.MTrainEM;
import programs.MTrainInit;
import programs.MTrainMAP;

public class EHMM {
	public static ClusterSet EHMMProcess(ClusterSet clusterSet, AudioFeatureSet featureSet, GMM ubm, Parameter parameter) 
			throws Exception{
		String mask = parameter.getParameterSegmentationOutputFile().getMask();
		parameter.getParameterModel().setModelKind("DIAG");
		parameter.getParameterModel().setNumberOfComponents(16);
		ubm.setName("ubm");
		Cluster longestCluster = null;
		int maxLength = 0;
		for(int i = 0;i < clusterSet.getClusterVectorRepresentation().size();i++){
			if(clusterSet.getClusterVectorRepresentation().get(i).getLength() > maxLength){
				maxLength = clusterSet.getClusterVectorRepresentation().get(i).getLength();
				longestCluster = clusterSet.getClusterVectorRepresentation().get(i);
			}
		}
		System.out.println(longestCluster.getName() + " "+longestCluster.getLength());
		GMM firstSpeakerGMM = ubm.clone();
		GMM secondSpeakerGMM = ubm.clone();
		firstSpeakerGMM.setName("0");
		firstSpeakerGMM = GMMFactory.getMAP(longestCluster, featureSet, firstSpeakerGMM, ubm, 
				parameter.getParameterEM(), parameter.getParameterMAP(), parameter.getParameterVarianceControl(), false, true);
		
		
		// first iteration
		GMMArrayList gmmVect = new GMMArrayList(2);
		gmmVect.add(firstSpeakerGMM);
		gmmVect.add(secondSpeakerGMM);//secondSpeakerGMM = ubm
		// ** set the penalty to move from the state i to the state j, penalty
		parameter.getParameterDecoder().setDecoderPenalty("5:1,5");//5:1,5
		// ** make Viterbi decoding using the 16-GMM set
		// ** one state = one GMM = one speaker = one cluster
		ClusterSet clustersDClustInital = MDecode.make(featureSet, clusterSet, gmmVect, parameter);
		
//		parameter.getParameterSegmentationOutputFile().setMask(mask.replace(".seg", "") + ".fi.seg");
//		MainTools.writeClusterSet(parameter, clustersDClustInital, false);
		
		
		parameter.getParameterSegmentationOutputFile().setMask(mask);
		//second iteration
		Cluster firstSpeaker = null;
		Cluster secondSpeaker = null;
		if(clustersDClustInital.getClusterVectorRepresentation().size() != 2){
			System.out.println("only one cluster!");
			return clustersDClustInital;
		}
		ClusterSet perviousClusterSet = clustersDClustInital.clone();
		ClusterSet currentClusterSet = clustersDClustInital.clone();
		parameter.getParameterDecoder().setDecoderPenalty("80,80");//80,80
		int it = 0;
		do{
			for(int i = 0;i < perviousClusterSet.getClusterVectorRepresentation().size();i++){
				if(perviousClusterSet.getClusterVectorRepresentation().get(i).getName().equals(longestCluster.getName())){
					firstSpeaker = perviousClusterSet.getClusterVectorRepresentation().get(i);
				}else{
					secondSpeaker = perviousClusterSet.getClusterVectorRepresentation().get(i);
				}
			}
//			ClusterSet twoMainClusters = new ClusterSet();
//			twoMainClusters.addCluster(firstSpeaker);
//			twoMainClusters.addCluster(secondSpeaker);
//			GMMArrayList gmmInitVect = new GMMArrayList(2);
//			MTrainInit.make(featureSet, twoMainClusters, gmmInitVect, parameter);
//			gmmVect.clear();
//			// ** EM training of the initialized GMM
//			MTrainEM.make(featureSet, twoMainClusters, gmmInitVect, gmmVect, parameter);
			firstSpeakerGMM = GMMFactory.getMAP(firstSpeaker, featureSet, firstSpeakerGMM, ubm, 
					parameter.getParameterEM(), parameter.getParameterMAP(), parameter.getParameterVarianceControl(), false, true);
			secondSpeakerGMM = GMMFactory.getMAP(secondSpeaker, featureSet, secondSpeakerGMM, ubm, 
					parameter.getParameterEM(), parameter.getParameterMAP(), parameter.getParameterVarianceControl(), false, true);
			gmmVect.clear();
			gmmVect.add(firstSpeakerGMM);
			gmmVect.add(secondSpeakerGMM);
			
			perviousClusterSet = currentClusterSet.clone();
			currentClusterSet = MDecode.make(featureSet, clusterSet, gmmVect, parameter);
			it++;
		}while(!currentClusterSet.equals(perviousClusterSet) && it < 10);
		
		
		return currentClusterSet;
	}
}
