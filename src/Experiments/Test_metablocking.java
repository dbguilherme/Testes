/**
 * @author @stravanni
 */
package Experiments;

import BlockBuilding.AbstractBlockingMethod;
import BlockBuilding.MemoryBased.TokenBlocking;
import BlockProcessing.BlockRefinement.BlockFiltering;
import BlockProcessing.BlockRefinement.ComparisonsBasedBlockPurging;
import BlockProcessing.ComparisonRefinement.AbstractDuplicatePropagation;
import BlockProcessing.ComparisonRefinement.BilateralDuplicatePropagation;
import BlockProcessing.ComparisonRefinement.UnilateralDuplicatePropagation;
import DataStructures.AbstractBlock;
import DataStructures.EntityProfile;
import Utilities.RepresentationModel;
import Utilities.SerializationUtilities;
import Experiments.Exp_Util;
import MetaBlocking.ThresholdWeightingScheme;
import MetaBlocking.WeightingScheme;
import OnTheFlyMethods.FastImplementations.BlastWeightedNodePruning;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

/**
 * @author stravanni
 */

public class Test_metablocking {

    private static boolean CLEAN = true;
    private static String BASEPATH_CER = "/Users/gio/Desktop/umich/data/data_blockingFramework/";
    private static String BASEPATH_DER = "/Users/gio/Desktop/umich/data/data_blockingFramework/";

    public static void main(String[] args) throws IOException {

    	
    	
    	
        int dataset = 0;
        boolean save = false;
        String blocking_type = "M";
        WeightingScheme ws = WeightingScheme.CHI_ENTRO;
       // WeightingScheme ws = WeightingScheme.FISHER_ENTRO; // For dirty dataset use this test-statistic because of the low number of co-occurrence in the blocks (Fisher exact test vs. Chi-squared ~ approximated)
        ThresholdWeightingScheme th_schme = ThresholdWeightingScheme.AM3;

        List<EntityProfile>[] profiles = new List[2];
//        if (args.length > 0) {
//            BASEPATH_CER = args[0] + "/";
//            BASEPATH_DER = args[0] + "/";
//            save = true;
//            profiles = Exp_Util.getEntities(BASEPATH_CER + "profiles/", dataset, CLEAN);
//        } else {
//            //profiles = Utilities.getEntities(BASEPATH, DATASET, CLEAN);
//            profiles = Exp_Util.getEntities(CLEAN ? BASEPATH_CER : BASEPATH_DER + "profiles/", dataset, CLEAN);
//        }

        
        
//        String mainDirectory = System.getProperty("user.home")+"/Dropbox/blocagem/bases/acm_clean";
//        String	profilesPathA= mainDirectory+"/dataset1_dblp";
//        String profilesPathB= mainDirectory+"/dataset2_acm";
        
        
        String  mainDirectory = System.getProperty("user.home")+"/Dropbox/blocagem/bases/movies";
//
        String  profilesPathA= mainDirectory+"/token/dataset1_imdb";
       String profilesPathB= mainDirectory+"/token/dataset2_dbpedia";
		String groundTruthPath =  mainDirectory+ "/groundtruth"; 
		groundTruthPath =  mainDirectory+ "/ground/groundtruth";
        
		
//		mainDirectory = System.getProperty("user.home")+"/Dropbox/blocagem/bases/base_clean_serializada";
//		profilesPathA= mainDirectory+"/dblp";
//		profilesPathB= mainDirectory+"/scholar";
//		groundTruthPath =  mainDirectory+ "/groundtruth"; 
//		
//		mainDirectory = System.getProperty("user.home")+"/Dropbox/blocagem/bases/cddb/";
//		profilesPathA =  mainDirectory+"/"+"cddbProfiles";
//		//profilesPathB =  mainDirectory+"/"+"dataset2_gp";
//		
//		groundTruthPath =  mainDirectory+"/"+"cddbIdDuplicates";	
		
		
	//	mainDirectory = System.getProperty("user.home")+"/Dropbox/blocagem/bases/produtos/";
	//	profilesPathA =  mainDirectory+"/"+"amazon"	;	
	//	groundTruthPath =  mainDirectory+"/"+"groundtruth";	
   //     
        profiles[0] = (List<EntityProfile>) SerializationUtilities.loadSerializedObject(profilesPathA);
		profiles[1] = (List<EntityProfile>) SerializationUtilities.loadSerializedObject(profilesPathB);
        //String groundTruthPath =  mainDirectory+ "/groundtruth";
        

		
        
//     	 profiles[0] = (List<EntityProfile>) SerializationUtilities.loadSerializedObject(profilesPathA);
//        String mainDirectory = System.getProperty("user.home")+"/Dropbox/blocagem/bases/sintetica";
//        String   profilesPathA =  mainDirectory+"/"+"10Kprofiles"	;	
//        String groundTruthPath =  mainDirectory+"/"+"10KIdDuplicates";	
//        profiles[0] = (List<EntityProfile>) SerializationUtilities.loadSerializedObject(profilesPathA);
       
        
        //List<EntityProfile>[] profiles = Utilities.getEntities(BASEPATH, DATASET, CLEAN);
        AbstractBlockingMethod blocking;

        Instant start = Instant.now();

        if (profiles.length > 1) {
            if (blocking_type == "T") {
                blocking = new TokenBlocking(new List[]{profiles[0], profiles[1]});
            } else {
                //blocking = new TokenBlocking(new List[]{profiles[0]});
                blocking = new BlockBuilding.MemoryBased.AttributeClusteringBlockingEntropy(RepresentationModel.TOKEN_SHINGLING, profiles, 120, 3, true);
                //blocking = new AttributeClusteringBlocking_original(RepresentationModel.TOKEN_UNIGRAMS, Exp_Util.getEntitiesPath(BASEPATH, dataset, CLEAN));
            }
        } else {
            System.out.println("\nok\n");
            //blocking = new TokenBlocking(new List[]{profiles[0]});
           blocking = new BlockBuilding.MemoryBased.AttributeClusteringBlockingEntropy(RepresentationModel.TOKEN_SHINGLING, profiles, 120, 3, true);
            //blocking = new AttributeClusteringBlocking(RepresentationModel.TOKEN_UNIGRAMS, new List[]{profiles[0]});

        }
     //   List<AbstractBlock> blocks = blocking.buildBlocks();


        double SMOOTHING_FACTOR = 1.015; // CLEAN
        //double SMOOTHING_FACTOR = 1.0; // CLEAN Dbpedia
        //double SMOOTHING_FACTOR = 1.015; // DIRTY
        double FILTERING_RATIO = 0.8;//0.99;
        
        // TODO NOTICE DOWN:
        //FOR CENSUS
//        double SMOOTHING_FACTOR = 1.05; // DIRTY
//        double FILTERING_RATIO = 1; //
double f1=0.0, pc=0.0, pq=0.0, smoot=0.0;
//for (SMOOTHING_FACTOR = 1; SMOOTHING_FACTOR < 1.3; SMOOTHING_FACTOR+=0.005) 
double Tpc=0.0, Tpq=0.0;
//for (int i = 0; i < 10; i++) 
{
	
	 // blocking = new TokenBlocking(new List[]{profiles[0], profiles[1]});

	
//	System.out.println("****************here ");
//	    blocking = new BlockBuilding.MemoryBased.AttributeClusteringBlockingEntropy(RepresentationModel.TOKEN_SHINGLING, profiles, 120, 3, true);
//	    List<AbstractBlock>  blocks = blocking.buildBlocks();
//        Instant start_purging = Instant.now();
//        System.out.println("blocking time: " + Duration.between(start, start_purging));
//
//        ComparisonsBasedBlockPurging cbbp = new ComparisonsBasedBlockPurging(SMOOTHING_FACTOR);
//        cbbp.applyProcessing(blocks);
//
//        System.out.println("\n01: " + blocks.get(0).getEntropy() + "\n\n");
//
//        BlockFiltering bf = new BlockFiltering(FILTERING_RATIO);
//        bf.applyProcessing(blocks);
//
//        System.out.println("\n02: " + blocks.get(0).getEntropy() + "\n\n");
//
//
//        System.out.println("n. of blocks: " + blocks.size());
//
//        AbstractDuplicatePropagation adp;// = Exp_Util.getGroundTruth(CLEAN ? BASEPATH_CER : BASEPATH_DER + "groundTruth/", dataset, CLEAN);
//        adp  =new UnilateralDuplicatePropagation(groundTruthPath); 
//        		//BilateralDuplicatePropagation(groundTruthPath);
//
//        Instant start_blast = Instant.now();
//
//        System.out.println("block purging_filtering time: " + Duration.between(start_purging, start_blast));
//
//        System.out.println("\nmain: " + blocks.get(0).getEntropy() + "\n\n");
//        BlastWeightedNodePruning b_wnp = new BlastWeightedNodePruning(adp, ws, th_schme, blocks.size());
//
//        //OnTheFlyMethods.FastImplementations.RedefinedWeightedNodePruning b_wnp = new OnTheFlyMethods.FastImplementations.RedefinedWeightedNodePruning(adp, ws, th_schme, blocks.size());
//        //OnTheFlyMethods.FastImplementations.ReciprocalWeightedNodePruning b_wnp = new OnTheFlyMethods.FastImplementations.ReciprocalWeightedNodePruning(adp, ws, th_schme, blocks.size());
//        //BlastWeightedNodePruning bwnp = new BlastWeightedNodePruning(adp, ws, th_schemes[0], blocks.size());
//        b_wnp.applyProcessing(blocks);
//
//        double[] values = b_wnp.getPerformance();
//
//        if(f1<(2 * values[0] * values[1]) / (values[0] + values[1])){
//        	f1=(2 * values[0] * values[1]) / (values[0] + values[1]);
//        	pc=values[0];
//        	pq=values[1];
//        }
//        System.out.println("pc: " + pc +"    "+ smoot);
//        System.out.println("pq: " + pq);
//        System.out.println("f1: " + f1);	
//
//        Instant end_blast = Instant.now();
//
//        System.out.println("blast time: " + Duration.between(start_blast, end_blast));
//        System.out.println("Total time: " + Duration.between(start, end_blast));
//    }
//System.out.println("FINALLL");
//
//System.out.println("pc: " + pc +"    "+ smoot);
//System.out.println("pq: " + pq);
//System.out.println("f1: " + f1);
	
	  Instant start_purging = Instant.now();
      System.out.println("blocking time: " + Duration.between(start, start_purging));
System.out.println("TOKEN ENTROPPY");
    //  List<AbstractBlock> blocks = null;
      blocking = new BlockBuilding.MemoryBased.AttributeClusteringBlockingEntropy(RepresentationModel.TOKEN_SHINGLING, profiles, 120, 3, true);
      List<AbstractBlock> blocks = blocking.buildBlocks();
      
      ComparisonsBasedBlockPurging cbbp = new ComparisonsBasedBlockPurging(SMOOTHING_FACTOR);
      
	  cbbp.applyProcessing(blocks);

	  //double[] values = cbbp.
	  
      System.out.println("\n01: " + blocks.get(0).getEntropy() + "\n\n");

      BlockFiltering bf = new BlockFiltering(FILTERING_RATIO);
      bf.applyProcessing(blocks);

    
      
     
      System.out.println("\n02: " + blocks.get(0).getEntropy() + "\n\n");


      System.out.println("n. of blocks: " + blocks.size());

      //AbstractDuplicatePropagation adp = Exp_Util.getGroundTruth(CLEAN ? BASEPATH_CER : BASEPATH_DER + "groundTruth/", dataset, CLEAN);

      AbstractDuplicatePropagation adp;// = Exp_Util.getGroundTruth(CLEAN ? BASEPATH_CER : BASEPATH_DER + "groundTruth/", dataset, CLEAN);
      adp  =new BilateralDuplicatePropagation(groundTruthPath); 

      Instant start_blast = Instant.now();

      System.out.println("block purging_filtering time: " + Duration.between(start_purging, start_blast));

      System.out.println("\nmain: " + blocks.get(0).getEntropy() + "\n\n");
      BlastWeightedNodePruning b_wnp = new BlastWeightedNodePruning(adp, ws, th_schme, blocks.size());

      //OnTheFlyMethods.FastImplementations.RedefinedWeightedNodePruning b_wnp = new OnTheFlyMethods.FastImplementations.RedefinedWeightedNodePruning(adp, ws, th_schme, blocks.size());
      //OnTheFlyMethods.FastImplementations.ReciprocalWeightedNodePruning b_wnp = new OnTheFlyMethods.FastImplementations.ReciprocalWeightedNodePruning(adp, ws, th_schme, blocks.size());
      //BlastWeightedNodePruning bwnp = new BlastWeightedNodePruning(adp, ws, th_schemes[0], blocks.size());
      b_wnp.applyProcessing(blocks);

      
      
      double[] values = b_wnp.getPerformance();

      System.out.println("pc: " + values[0]);
      System.out.println("pq: " + values[1]);
      System.out.println("f1: " + (2 * values[0] * values[1]) / (values[0] + values[1]));
      Tpc+=values[0];
      Tpq+=values[1];
      
      Instant end_blast = Instant.now();

      System.out.println("blast time: " + Duration.between(start_blast, end_blast));
      System.out.println("Total time: " + Duration.between(start, end_blast));

   // }
  //  }
    }

	System.out.println("total tpc "+ Tpc/10 + " ");
	System.out.println("total tpc "+ Tpq/10);
}
}