package desperados.scb;

import java.util.LinkedHashMap;
import java.util.Map;

public class ScbExternalFunction {

	static Map<String, Integer> functionNames;
	static {
		functionNames = new LinkedHashMap<>(200);
		functionNames.put("StartDialog", 0x00);
		functionNames.put("SetCameraScrollTo", 0x01);
		functionNames.put("SetCameraScrollSlowlyTo", 0x02);
		functionNames.put("SetCameraJumpTo", 0x03);
		functionNames.put("SetZoomLevel", 0x04);
		functionNames.put("DisplayMap", 0x05);
		functionNames.put("PrintConsole", 0x06);
		functionNames.put("DisplayConsole", 0x07);
		functionNames.put("IsActorEqual", 0x08);
		functionNames.put("IsAnyCivilianDead", 0x09);
		functionNames.put("IsAnyEnemyDead", 0x0A);
		functionNames.put("GetOverallEnemyAlert", 0x0B);
		functionNames.put("GetOverallCivilianAlert", 0x0C);
		functionNames.put("FreezeAll", 0x0D);
		functionNames.put("InflictPain", 0x0E);
		functionNames.put("GetNumberOfActorsInEngine", 0x0F);
		functionNames.put("GetActor", 0x10);
		functionNames.put("GetLocation", 0x11);
		functionNames.put("IsActorAnimation", 0x12);
		functionNames.put("IsActorObject", 0x13);
		functionNames.put("IsActorCharacter", 0x14);
		functionNames.put("IsActorPC", 0x15);
		functionNames.put("IsActorNPC", 0x16);
		functionNames.put("IsActorEnemy", 0x17);
		functionNames.put("IsActorCivilian", 0x18);
		functionNames.put("IsActorAnimal", 0x19);
		functionNames.put("IsNull", 0x1A);
		functionNames.put("GetActorPosture", 0x1B);
		functionNames.put("SetActorPosture", 0x1C);
		functionNames.put("GetCooper", 0x1D);
		functionNames.put("GetSam", 0x1E);
		functionNames.put("GetDoc", 0x1F);
		functionNames.put("GetKate", 0x20);
		functionNames.put("GetSanchez", 0x21);
		functionNames.put("GetMia", 0x22);
		functionNames.put("GetActorLocation", 0x23);
		functionNames.put("SetActorLocation", 0x24);
		functionNames.put("IsInside", 0x25);
		functionNames.put("IsInsideBuilding", 0x26);
		functionNames.put("GetAnyActorInside", 0x27);
		functionNames.put("SwitchProfile", 0x28);
		functionNames.put("GetMovementStyle", 0x29);
		functionNames.put("Deactivate", 0x2A);
		functionNames.put("Activate", 0x2B);
		functionNames.put("SetActionAvailable", 0x2C);
		functionNames.put("IsActionAvailable", 0x2D);
		functionNames.put("SetPersistentProperty", 0x2E);
		functionNames.put("GetPersistentProperty", 0x2F);
		functionNames.put("SetAIAlertStatus", 0x30);
		functionNames.put("GetAIAlertStatus", 0x31);
		functionNames.put("SetAIState", 0x32);
		functionNames.put("GetAIState", 0x33);
		functionNames.put("SetAIAttitude", 0x34);
		functionNames.put("GetAIAttitude", 0x35);
		functionNames.put("SetAILevel", 0x36);
		functionNames.put("SetViewCone", 0x37);
		functionNames.put("StareActor", 0x38);
		functionNames.put("StareLocation", 0x39);
		functionNames.put("AssignPath", 0x3A);
		functionNames.put("AssignPost", 0x3B);
		functionNames.put("LockAI", 0x3C);
		functionNames.put("UnlockAI", 0x3D);
		functionNames.put("ForceBattleDecision", 0x3E);
		functionNames.put("MakeNoise", 0x3F);
		functionNames.put("Freeze", 0x40);
		functionNames.put("GetTheRider", 0x41);
		functionNames.put("GetTheHorse", 0x42);
		functionNames.put("IsAnimationActive", 0x43);
		functionNames.put("SetAnimationState", 0x44);
		functionNames.put("IsPatchApplied", 0x45);
		functionNames.put("ApplyPatch", 0x46);
		functionNames.put("IsHorseUsed", 0x47);
		functionNames.put("StartSequenceRecording", 0x48);
		functionNames.put("EndSequenceRecording", 0x49);
		functionNames.put("IncrementSequenceLevel", 0x4A);
		functionNames.put("Start", 0x4B);
		functionNames.put("Thanx", 0x4C);
		functionNames.put("Then", 0x4D);
		functionNames.put("RecordCameraScrollTo", 0x4E);
		functionNames.put("RecordCameraJumpTo", 0x4F);
		functionNames.put("RecordSetZoom", 0x50);
		functionNames.put("RecordDisplayMap", 0x51);
		functionNames.put("RecordStartBriefing", 0x52);
		functionNames.put("RecordEndBriefing", 0x53);
		functionNames.put("RecordActionAvailable", 0x54);
		functionNames.put("RecordCharacterAvailable", 0x55);
		functionNames.put("RecordLockCameraOn", 0x56);
		functionNames.put("RecordClearCameraOn", 0x57);
		functionNames.put("RecordPlayDialog", 0x58);
		functionNames.put("RecordMove", 0x59);
		functionNames.put("RecordLeaveGame", 0x5A);
		functionNames.put("RecordTurnTo", 0x5B);
		functionNames.put("RecordMountHorse", 0x5C);
		functionNames.put("RecordDismountHorse", 0x5D);
		functionNames.put("RecordFireAt", 0x5E);
		functionNames.put("RecordPlayAnim", 0x5F);
		functionNames.put("RecordPlayAnimLoop", 0x60);
		functionNames.put("RecordPlayAnimFreeze", 0x61);
		functionNames.put("RecordLockAI", 0x62);
		functionNames.put("RecordUnlockAI", 0x63);
		functionNames.put("RecordLockUser", 0x64);
		functionNames.put("RecordUnLockUser", 0x65);
		functionNames.put("RecordTimer", 0x66);
		functionNames.put("RecordSeekActor", 0x67);
		functionNames.put("RecordStopSeek", 0x68);
		functionNames.put("RecordSendCustomEvent", 0x69);
		functionNames.put("RecordAction", 0x6A);
		functionNames.put("InitGlobal", 0x6B);
		functionNames.put("SetGlobal", 0x6C);
		functionNames.put("GetGlobal", 0x6D);
		functionNames.put("SuspendAllSoundSources", 0x6E);
		functionNames.put("ResumeAllSoundSources", 0x6F);
		functionNames.put("GetSoundSource", 0x70);
		functionNames.put("ActivateSoundSource", 0x71);
		functionNames.put("DeactivateSoundSource", 0x72);
		functionNames.put("DestroySoundSource", 0x73);
		functionNames.put("CleanFromHisBuildingBeforeTeleport", 0x74);
		functionNames.put("AddToHisNewBuildingAfterTeleport", 0x75);
		functionNames.put("CleanFromScriptZoneBeforeTeleport", 0x76);
		functionNames.put("AddToScriptZoneAfterTeleport", 0x77);
		functionNames.put("LockNearestDoorForPCs", 0x78);
		functionNames.put("LockNearestDoorForVillains", 0x79);
		functionNames.put("LockNearestDoorForCivilians", 0x7A);
		functionNames.put("This", 0x7B);
		functionNames.put("GetActorDirection", 0x7C);
		functionNames.put("SetActorDirection", 0x7D);
		functionNames.put("StopActor", 0x7E);
		functionNames.put("GetDistance", 0x7F);
		functionNames.put("GetCurrentAction", 0x80);
		functionNames.put("IsActorCart", 0x81);
		functionNames.put("SetPathWalkingStyle", 0x82);
		functionNames.put("RecordReplaceAnim", 0x83);
		functionNames.put("RecordRestoreAnim", 0x84);
		functionNames.put("Sees", 0x85);
		functionNames.put("GetActorIndex", 0x86);
		functionNames.put("JoinArmy", 0x87);
		functionNames.put("DeclareWar", 0x88);
		functionNames.put("GetArmy", 0x89);
		functionNames.put("SetSoldierSeesSoldier", 0x8A);
		functionNames.put("SetSoldierSeesArmy", 0x8B);
		functionNames.put("SetArmySeesSoldier", 0x8C);
		functionNames.put("SetArmySeesArmy", 0x8D);
		functionNames.put("GetNumberOfVisibleSoldiers", 0x8E);
		functionNames.put("GetVisibleSoldier", 0x8F);
		functionNames.put("GetNearestVisibleSoldier", 0x90);
		functionNames.put("CreateListOfRecentlySeenSoldiers", 0x91);
		functionNames.put("GetNumberOfRecentlySeenSoldiers", 0x92);
		functionNames.put("GetRecentlySeenSoldier", 0x93);
		functionNames.put("GetNearestRecentlySeenSoldier", 0x94);
		functionNames.put("PutSnakeOnMap", 0x95);
		functionNames.put("PutWatchOnMap", 0x96);
		functionNames.put("RecordTakeCorpse", 0x97);
		functionNames.put("RecordMoveIntoBuilding", 0x98);
		functionNames.put("RecordCameraMoveTo", 0x99);
		functionNames.put("RecordEnterBarrel", 0x9A);
		functionNames.put("RecordLeaveBarrel", 0x9B);
		functionNames.put("RecordEnterGame", 0x9C);
		functionNames.put("RecordLeaveCorpse", 0x9D);
		functionNames.put("SetUltimateWillOnHorse", 0x9E);
		functionNames.put("CallKnife", 0x9F);
		functionNames.put("ResetAnim", 0xA0);
		functionNames.put("RecordJumpOnHorse", 0xA1);
		functionNames.put("EnableHorseUseFor", 0xA2);
		functionNames.put("DefineAnimalAlert", 0xA3);
		functionNames.put("RecordTieCorpse", 0xA4);
		functionNames.put("PutActorInBuilding", 0xA5);
		functionNames.put("SetBuildingActive", 0xA6);
		functionNames.put("RecordStartMobileElement", 0xA7);
		functionNames.put("RecordStopMobileElement", 0xA8);
		functionNames.put("SetNearestDoorDocLockPickable", 0xA9);
		functionNames.put("DisplayHint", 0xAA);
		functionNames.put("HideHint", 0xAB);
		functionNames.put("PutActorInBarrel", 0xAC);
		functionNames.put("SetSkipData", 0xAD);
		functionNames.put("ForbidHorseForAI", 0xAE);
		functionNames.put("RecordSpeak", 0xAF);
		functionNames.put("RecordFireLocation", 0xB0);
		functionNames.put("Rand", 0xB1);
		functionNames.put("ResetPatch", 0xB2);
		functionNames.put("PrototypeFilterEvent", 0xB3);
		functionNames.put("AddSentence", 0xB4);
		functionNames.put("DoneSentence", 0xB5);
		functionNames.put("ChooseVictoryDefeatText", 0xB6);
		functionNames.put("EnableViewCone", 0xB7);
		functionNames.put("SetPCAsUnwantedFor", 0xB8);
		functionNames.put("SpecialAutorisation", 0xB9);
		functionNames.put("RecordSpeakPC", 0xBA);
		functionNames.put("SelectPC", 0xBB);
		functionNames.put("SetBuildingAsTrainWaggon", 0xBC);
		functionNames.put("RecordSeekActorCustomEvent", 0xBD);
		functionNames.put("ActivateDoorMouseSector", 0xBE);
		functionNames.put("GetOutlineDisplay", 0xBF);
		functionNames.put("SetOutlineDisplay", 0xC0);
		functionNames.put("CustomizeMinimapDisplay", 0xC1);
		functionNames.put("GetDoorStateForPC", 0xC2);
		functionNames.put("RecordLeaveGameDirectionnal", 0xC3);
		functionNames.put("SetCorpseExistsInBuilding", 0xC4);
		functionNames.put("ForceCheckVictory", 0xC5);
		functionNames.put("ForceEmergencyBoxForMotionArea", 0xC6);
		functionNames.put("SetUserInputPossibleForHorse", 0xC7);
	}

	static String[] functions = new String[200];
	static {
		functions[0x00] /*  0*/ = "StartDialog(int dialogId)";
		functions[0x01] /*  1*/ = "SetCameraScrollTo(object)";
		functions[0x02] /*  2*/ = "SetCameraScrollSlowlyTo(object, float)";
		functions[0x03] /*  3*/ = "SetCameraJumpTo(object)";
		functions[0x04] /*  4*/ = "SetZoomLevel(float)";
		functions[0x05] /*  5*/ = "DisplayMap(bool)";
		functions[0x06] /*  6*/ = "PrintConsole(int)";
		functions[0x07] /*  7*/ = "DisplayConsole()";	/* unused */
		functions[0x08] /*  8*/ = "IsActorEqual(actor, actor)";
		functions[0x09] /*  9*/ = "IsAnyCivilianDead()";	/* unused */
		functions[0x0A] /* 10*/ = "IsAnyEnemyDead()";
		functions[0x0B] /* 11*/ = "GetOverallEnemyAlert()";
		functions[0x0C] /* 12*/ = "GetOverallCivilianAlert()";	/* unused */
		functions[0x0D] /* 13*/ = "FreezeAll(bool)";	/* unused */
		functions[0x0E] /* 14*/ = "InflictPain(actor, int damage, bool knockOut)";
		functions[0x0F] /* 15*/ = "GetNumberOfActorsInEngine()";
		functions[0x10] /* 16*/ = "GetActor(int actorId)";
		functions[0x11] /* 17*/ = "GetLocation(int locationId)";
		functions[0x12] /* 18*/ = "IsActorAnimation(object)";	/* unused */
		functions[0x13] /* 19*/ = "IsActorObject(actor)";	/* unused */
		functions[0x14] /* 20*/ = "IsActorCharacter(actor)";
		functions[0x15] /* 21*/ = "IsActorPC(actor)";
		functions[0x16] /* 22*/ = "IsActorNPC(actor)";
		functions[0x17] /* 23*/ = "IsActorEnemy(actor)";
		functions[0x18] /* 24*/ = "IsActorCivilian(actor)";
		functions[0x19] /* 25*/ = "IsActorAnimal(actor)";
		functions[0x1A] /* 26*/ = "IsNull(object)";
		functions[0x1B] /* 27*/ = "GetActorPosture(actor)";
		functions[0x1C] /* 28*/ = "SetActorPosture(actor, int)"; 
		functions[0x1D] /* 29*/ = "GetCooper()";
		functions[0x1E] /* 30*/ = "GetSam()";
		functions[0x1F] /* 31*/ = "GetDoc()";
		functions[0x20] /* 32*/ = "GetKate()";
		functions[0x21] /* 33*/ = "GetSanchez()";
		functions[0x22] /* 34*/ = "GetMia()";
		functions[0x23] /* 35*/ = "GetActorLocation(actor)";
		functions[0x24] /* 36*/ = "SetActorLocation(actor, location)";
		functions[0x25] /* 37*/ = "IsInside(actor, location)";
		functions[0x26] /* 38*/ = "IsInsideBuilding(actor, int)";
		functions[0x27] /* 39*/ = "GetAnyActorInside(location)";	/* unused */
		functions[0x28] /* 40*/ = "SwitchProfile(object)";
		functions[0x29] /* 41*/ = "GetMovementStyle(actor)";	/* unused */
		functions[0x2A] /* 42*/ = "Deactivate(actor)";
		functions[0x2B] /* 43*/ = "Activate(actor)";
		functions[0x2C] /* 44*/ = "SetActionAvailable(actor, int action, bool)";
		functions[0x2D] /* 45*/ = "IsActionAvailable(actor, int action)";
		functions[0x2E] /* 46*/ = "SetPersistentProperty(actor, int property, int)";
		functions[0x2F] /* 47*/ = "GetPersistentProperty(actor, int property)";
		functions[0x30] /* 48*/ = "SetAIAlertStatus(actor, int)";
		functions[0x31] /* 49*/ = "GetAIAlertStatus(actor)";
		functions[0x32] /* 50*/ = "SetAIState(actor, int)";
		functions[0x33] /* 51*/ = "GetAIState(actor)";
		functions[0x34] /* 52*/ = "SetAIAttitude(actor, int)";
		functions[0x35] /* 53*/ = "GetAIAttitude(actor)";
		functions[0x36] /* 54*/ = "SetAILevel(actor, int, int)";
		functions[0x37] /* 55*/ = "SetViewCone(actor, int)";
		functions[0x38] /* 56*/ = "StareActor(actor, object, bool)";
		functions[0x39] /* 57*/ = "StareLocation(actor, object, bool)";
		functions[0x3A] /* 58*/ = "AssignPath(actor, int)";
		functions[0x3B] /* 59*/ = "AssignPost(actor, location, int)";
		functions[0x3C] /* 60*/ = "LockAI(actor, bool)";
		functions[0x3D] /* 61*/ = "UnlockAI(actor)";
		functions[0x3E] /* 62*/ = "ForceBattleDecision(actor, int)";
		functions[0x3F] /* 63*/ = "MakeNoise(object, int)";
		functions[0x40] /* 64*/ = "Freeze(actor, bool)";	/* unused */
		functions[0x41] /* 65*/ = "GetTheRider(object)";
		functions[0x42] /* 66*/ = "GetTheHorse(object)";
		functions[0x43] /* 67*/ = "IsAnimationActive(object)";
		functions[0x44] /* 68*/ = "SetAnimationState(object, bool)";
		functions[0x45] /* 69*/ = "IsPatchApplied(int)";
		functions[0x46] /* 70*/ = "ApplyPatch(int)";
		functions[0x47] /* 71*/ = "IsHorseUsed(object)";
		functions[0x48] /* 72*/ = "StartSequenceRecording()";
		functions[0x49] /* 73*/ = "EndSequenceRecording()";
		functions[0x4A] /* 74*/ = "IncrementSequenceLevel()";
		functions[0x4B] /* 75*/ = "Start()";
		functions[0x4C] /* 76*/ = "Thanx()";
		functions[0x4D] /* 77*/ = "Then()";
		functions[0x4E] /* 78*/ = "RecordCameraScrollTo(location)";
		functions[0x4F] /* 79*/ = "RecordCameraJumpTo(location)";
		functions[0x50] /* 80*/ = "RecordSetZoom(float)";
		functions[0x51] /* 81*/ = "RecordDisplayMap(bool)";	/* unused */
		functions[0x52] /* 82*/ = "RecordStartBriefing()";
		functions[0x53] /* 83*/ = "RecordEndBriefing()";
		functions[0x54] /* 84*/ = "RecordActionAvailable(object, int, bool)";
		functions[0x55] /* 85*/ = "RecordCharacterAvailable(object, bool)";	/* unused */
		functions[0x56] /* 86*/ = "RecordLockCameraOn(object)";
		functions[0x57] /* 87*/ = "RecordClearCameraOn()";
		functions[0x58] /* 88*/ = "RecordPlayDialog(int)";
		functions[0x59] /* 89*/ = "RecordMove(actor, location, int)"; // 0 = walking, 1 = running
		functions[0x5A] /* 90*/ = "RecordLeaveGame(object, object, int)";
		functions[0x5B] /* 91*/ = "RecordTurnTo(actor, location)";
		functions[0x5C] /* 92*/ = "RecordMountHorse(object, object)";
		functions[0x5D] /* 93*/ = "RecordDismountHorse(object, object)";
		functions[0x5E] /* 94*/ = "RecordFireAt(object, object)";
		functions[0x5F] /* 95*/ = "RecordPlayAnim(actor, int)"; // 1 = standing, 3 = slow walking, 4 = fast walking, 5 = running, 7 = laying on ground
		functions[0x60] /* 96*/ = "RecordPlayAnimLoop(actor, int)";
		functions[0x61] /* 97*/ = "RecordPlayAnimFreeze(actor, int)";
		functions[0x62] /* 98*/ = "RecordLockAI(actor)";
		functions[0x63] /* 99*/ = "RecordUnlockAI()";
		functions[0x64] /*100*/ = "RecordLockUser()";
		functions[0x65] /*101*/ = "RecordUnLockUser(object)";
		functions[0x66] /*102*/ = "RecordTimer(int ticks)";
		functions[0x67] /*103*/ = "RecordSeekActor(object, object, int, float)";
		functions[0x68] /*104*/ = "RecordStopSeek(object)";	/* unused */
		functions[0x69] /*105*/ = "RecordSendCustomEvent(object, int)";
		functions[0x6A] /*106*/ = "RecordAction(actor, int, int)";
		functions[0x6B] /*107*/ = "InitGlobal(int index, int value)";
		functions[0x6C] /*108*/ = "SetGlobal(int index, int value)";
		functions[0x6D] /*109*/ = "GetGlobal(int index)";
		functions[0x6E] /*110*/ = "SuspendAllSoundSources()";	/* unused */
		functions[0x6F] /*111*/ = "ResumeAllSoundSources()";	/* unused */
		functions[0x70] /*112*/ = "GetSoundSource(int)";
		functions[0x71] /*113*/ = "ActivateSoundSource(object)";
		functions[0x72] /*114*/ = "DeactivateSoundSource(object)";
		functions[0x73] /*115*/ = "DestroySoundSource(object)";	/* unused */
		functions[0x74] /*116*/ = "CleanFromHisBuildingBeforeTeleport(actor)";
		functions[0x75] /*117*/ = "AddToHisNewBuildingAfterTeleport(actor)";	/* unused */
		functions[0x76] /*118*/ = "CleanFromScriptZoneBeforeTeleport(actor, object)";	/* unused */
		functions[0x77] /*119*/ = "AddToScriptZoneAfterTeleport(actor, object)";
		functions[0x78] /*120*/ = "LockNearestDoorForPCs(object, bool)";
		functions[0x79] /*121*/ = "LockNearestDoorForVillains(object, bool)";
		functions[0x7A] /*122*/ = "LockNearestDoorForCivilians(object, bool)";
		functions[0x7B] /*123*/ = "This()";
		functions[0x7C] /*124*/ = "GetActorDirection(actor)";
		functions[0x7D] /*125*/ = "SetActorDirection(actor, int directionId)";
		functions[0x7E] /*126*/ = "StopActor(actor)";
		functions[0x7F] /*127*/ = "GetDistance(object, object)";
		functions[0x80] /*128*/ = "GetCurrentAction(object)";
		functions[0x81] /*129*/ = "IsActorCart(actor)";	/* unused */
		functions[0x82] /*130*/ = "SetPathWalkingStyle(object, int)";
		functions[0x83] /*131*/ = "RecordReplaceAnim(object, int, int)";
		functions[0x84] /*132*/ = "RecordRestoreAnim(object, int)";
		functions[0x85] /*133*/ = "Sees(actor, actor)";	/* unused */
		functions[0x86] /*134*/ = "GetActorIndex(actor)";
		functions[0x87] /*135*/ = "JoinArmy(object, int)";	/* unused */
		functions[0x88] /*136*/ = "DeclareWar(object)";
		functions[0x89] /*137*/ = "GetArmy(object)";
		functions[0x8A] /*138*/ = "SetSoldierSeesSoldier(object, object, bool)";	/* unused */
		functions[0x8B] /*139*/ = "SetSoldierSeesArmy(object, int, bool)";
		functions[0x8C] /*140*/ = "SetArmySeesSoldier(int, object, bool)";
		functions[0x8D] /*141*/ = "SetArmySeesArmy(int, int, bool)";	/* unused */
		functions[0x8E] /*142*/ = "GetNumberOfVisibleSoldiers(object)";
		functions[0x8F] /*143*/ = "GetVisibleSoldier(object, int)";	/* unused */
		functions[0x90] /*144*/ = "GetNearestVisibleSoldier(object)";
		functions[0x91] /*145*/ = "CreateListOfRecentlySeenSoldiers(object, int)";	/* unused */
		functions[0x92] /*146*/ = "GetNumberOfRecentlySeenSoldiers()";
		functions[0x93] /*147*/ = "GetRecentlySeenSoldier(int)";	/* unused */
		functions[0x94] /*148*/ = "GetNearestRecentlySeenSoldier()";	/* unused */
		functions[0x95] /*149*/ = "PutSnakeOnMap(object)";	/* unused */
		functions[0x96] /*150*/ = "PutWatchOnMap(object)";
		functions[0x97] /*151*/ = "RecordTakeCorpse(object, object, int)";
		functions[0x98] /*152*/ = "RecordMoveIntoBuilding(object, object, int)";
		functions[0x99] /*153*/ = "RecordCameraMoveTo(location, int speed)";
		functions[0x9A] /*154*/ = "RecordEnterBarrel(object, object, int)";
		functions[0x9B] /*155*/ = "RecordLeaveBarrel(object)";
		functions[0x9C] /*156*/ = "RecordEnterGame(object, object, object, int)";
		functions[0x9D] /*157*/ = "RecordLeaveCorpse(object)";
		functions[0x9E] /*158*/ = "SetUltimateWillOnHorse(object, bool)";
		functions[0x9F] /*159*/ = "CallKnife()";
		functions[0xA0] /*160*/ = "ResetAnim(object)";
		functions[0xA1] /*161*/ = "RecordJumpOnHorse(object, object)";
		functions[0xA2] /*162*/ = "EnableHorseUseFor(object, bool)";
		functions[0xA3] /*163*/ = "DefineAnimalAlert(object, object, int)";
		functions[0xA4] /*164*/ = "RecordTieCorpse(object, object, int)";
		functions[0xA5] /*165*/ = "PutActorInBuilding(actor, int)";
		functions[0xA6] /*166*/ = "SetBuildingActive(int, bool)";
		functions[0xA7] /*167*/ = "RecordStartMobileElement(int)";
		functions[0xA8] /*168*/ = "RecordStopMobileElement(int)";
		functions[0xA9] /*169*/ = "SetNearestDoorDocLockPickable(object, bool)";
		functions[0xAA] /*170*/ = "DisplayHint(int)";
		functions[0xAB] /*171*/ = "HideHint()";
		functions[0xAC] /*172*/ = "PutActorInBarrel(actor, object)";
		functions[0xAD] /*173*/ = "SetSkipData(int)";
		functions[0xAE] /*174*/ = "ForbidHorseForAI(actor, bool)";
		functions[0xAF] /*175*/ = "RecordSpeak(actor, int)";
		functions[0xB0] /*176*/ = "RecordFireLocation(object, object)";
		functions[0xB1] /*177*/ = "Rand(int)";
		functions[0xB2] /*178*/ = "ResetPatch(int)";
		functions[0xB3] /*179*/ = "PrototypeFilterEvent(object, object, int)";
		functions[0xB4] /*180*/ = "AddSentence(int)"; // sentence in briefing notes
		functions[0xB5] /*181*/ = "DoneSentence(int)";
		functions[0xB6] /*182*/ = "ChooseVictoryDefeatText(int)";
		functions[0xB7] /*183*/ = "EnableViewCone(object)";
		functions[0xB8] /*184*/ = "SetPCAsUnwantedFor(object, object)";
		functions[0xB9] /*185*/ = "SpecialAutorisation(object, bool, object)";
		functions[0xBA] /*186*/ = "RecordSpeakPC(object, int)";
		functions[0xBB] /*187*/ = "SelectPC(int)";
		functions[0xBC] /*188*/ = "SetBuildingAsTrainWaggon(int, int)";
		functions[0xBD] /*189*/ = "RecordSeekActorCustomEvent(object, object, int, float, object, int)";
		functions[0xBE] /*190*/ = "ActivateDoorMouseSector(bool, object)";
		functions[0xBF] /*191*/ = "GetOutlineDisplay()";	/* unused */
		functions[0xC0] /*192*/ = "SetOutlineDisplay(bool)";
		// Script Error: Minimap display codes 111, 222, 333 only valid for humans.
		functions[0xC1] /*193*/ = "CustomizeMinimapDisplay(actor, int)"; // 0 = none, 100 = civilian(green), 200 = enemy(red), 300 = ally(green), 400 = animal(gray), 500 = item(+)
		functions[0xC2] /*194*/ = "GetDoorStateForPC(object)";
		functions[0xC3] /*195*/ = "RecordLeaveGameDirectionnal(object, int, int)";
		functions[0xC4] /*196*/ = "SetCorpseExistsInBuilding(object)";
		functions[0xC5] /*197*/ = "ForceCheckVictory()";
		functions[0xC6] /*198*/ = "ForceEmergencyBoxForMotionArea(object)";
		functions[0xC7] /*199*/ = "SetUserInputPossibleForHorse(object, bool)";
	}
}
