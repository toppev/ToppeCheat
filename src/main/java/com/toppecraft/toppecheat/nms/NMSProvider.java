package com.toppecraft.toppecheat.nms;

import com.toppecraft.toppecheat.ToppeCheat;
import com.toppecraft.toppecheat.nms.access.NMSAccess;
import com.toppecraft.toppecheat.nms.access.NMS_1_8_R3;
import org.bukkit.Bukkit;

public class NMSProvider {

    private NMSAccess access;

    public void setup() {
        String version = Bukkit.getServer().getClass().getPackage()
                               .getName().substring(23);
        // To make it easier to use maven removed support for other versions
        // Feel free to implement them
        switch (version) {
			/*
		case "v1_7_R2":
			access = new NMS_1_7_R2();
			break;
		case "v1_7_R3":
			access = new NMS_1_7_R3();
			break;
            case "v1_7_R4":
                access = new NMS_1_7_R4();
                break;
		case "v1_8_R1":
			access = new NMS_1_8_R1();
			break;
		case "v1_8_R2":
			access = new NMS_1_8_R2();
			break;
			*/
            case "v1_8_R3":
                access = new NMS_1_8_R3();
                break;
			/*
		case "v1_9_R1":
			access = new NMS_1_9_R1();
			break;
		case "v1_9_R2":
			access = new NMS_1_9_R2();
			break;

			 */
            default:
                Bukkit.getLogger().warning("TAC >> Not supported version! Disabling the plugin!");
                Bukkit.getPluginManager().disablePlugin(ToppeCheat.getInstance());
                break;
        }
        if (access != null) {
            Bukkit.getLogger().info("TAC >> Version supported! (" + access.getClass().getSimpleName() + ")");
        }
    }

    public NMSAccess getAccess() {
        return access;
    }
}
