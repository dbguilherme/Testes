package Experiments;

import BlockBuilding.AbstractTokenBlocking;
import BlockBuilding.MemoryBased.TokenBlocking;
import BlockProcessing.ComparisonRefinement.AbstractDuplicatePropagation;
import BlockProcessing.ComparisonRefinement.BilateralDuplicatePropagation;
import DataStructures.AbstractBlock;
import DataStructures.EntityProfile;
import Utilities.RepresentationModel;
import Utilities.SerializationUtilities;
import Utilities.BlockStatistics;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * @author giovanni
 */
public class LSH_AttributeMatchInduction {

    private static int DATASET = 2;
    private static boolean CLEAN = true;
    private static String BASEPATH = "/Users/gio/Desktop/umich/data/data_blockingFramework/";

    public static void main(String[] args) {

        Instant start = Instant.now();

        List<EntityProfile>[] profiles= new List[2];
        AbstractDuplicatePropagation adp;
//        if (args.length > 0) {
//            BASEPATH = args[0] + "/";
//            //profiles = Utilities.getEntities(BASEPATH + "profiles/", DATASET, CLEAN);
//            profiles = Utilities.getEntities(BASEPATH, DATASET, CLEAN);
//            adp = Utilities.getGroundTruth(BASEPATH, DATASET, CLEAN);
//        } else {
//            profiles = Utilities.getEntities(DATASET, CLEAN);
//            adp = Utilities.getGroundTruth(DATASET, CLEAN);
//        }
        String  mainDirectory = System.getProperty("user.home")+"/Dropbox/blocagem/bases/movies";

        String  profilesPathA= mainDirectory+"/token/dataset1_imdb";
        String profilesPathB= mainDirectory+"/token/dataset2_dbpedia";
		String groundTruthPath =  mainDirectory+ "/groundtruth"; 
		groundTruthPath =  mainDirectory+ "/ground/groundtruth";
		profiles[0] = (List<EntityProfile>) SerializationUtilities.loadSerializedObject(profilesPathA);
		profiles[1] = (List<EntityProfile>) SerializationUtilities.loadSerializedObject(profilesPathB);

		adp  =new BilateralDuplicatePropagation(groundTruthPath); 
        //BlockBuilding.MemoryBased.AttributeClusteringBlocking ac = new BlockBuilding.MemoryBased.AttributeClusteringBlocking(RepresentationModel.TOKEN_SHINGLING, new List[]{profiles[0]});
        //BlockBuilding.MemoryBased.AttributeClusteringBlocking ac = new BlockBuilding.MemoryBased.AttributeClusteringBlocking(RepresentationModel.TOKEN_SHINGLING, profiles);
        //BlockBuilding.MemoryBased.AttributeClusteringBlocking ac = new BlockBuilding.MemoryBased.AttributeClusteringBlocking(RepresentationModel.TOKEN_UNIGRAMS, profiles);
        //TokenBlocking ac = new TokenBlocking(profiles);

        AbstractTokenBlocking ac = null;
        if (profiles.length > 1) {
            //ac = new TokenBlocking(profiles);
            ac = new BlockBuilding.MemoryBased.AttributeClusteringBlockingEntropy(RepresentationModel.TOKEN_SHINGLING, profiles, 120, 3, true);
            //ac = new BlockBuilding.MemoryBased.AttributeClusteringBlocking(RepresentationModel.TOKEN_SHINGLING, profiles, 120, 3, false);
            //ac = new BlockBuilding.MemoryBased.AttributeClusteringBlocking(RepresentationModel.TOKEN_UNIGRAMS, profiles);
        } else {
            //ac = new TokenBlocking(new List[]{profiles[0]});
            //ac = new BlockBuilding.MemoryBased.AttributeClusteringBlocking(RepresentationModel.TOKEN_UNIGRAMS, new List[]{profiles[0]});
            //ac = new BlockBuilding.MemoryBased.AttributeClusteringBlocking(RepresentationModel.TOKEN_SHINGLING, profiles, 120, 3, false);
            ac = new BlockBuilding.MemoryBased.AttributeClusteringBlockingEntropy(RepresentationModel.TOKEN_SHINGLING, profiles, 120, 3, false);
        }

        List<AbstractBlock> blocks = ac.buildBlocks();

        //AbstractDuplicatePropagation adp = Utilities.getGroundTruth(BASEPATH, DATASET, CLEAN);
        //AbstractDuplicatePropagation adp = Utilities.getGroundTruth(DATASET, CLEAN);
        BlockStatistics bStats1 = new BlockStatistics(blocks, adp);
        double[] values = bStats1.applyProcessing();

        Instant end = Instant.now();

        System.out.println("Total time: " + Duration.between(start, end).toString());
    }

}