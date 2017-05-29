package com.lumen.constants;

/**
 * Created by biphilip on 10/3/15.
 */
public enum AppNames{
    TALLY_TOTS("air.com.spinlight.tallytots"),
    KIDS_ABC_PHONICS("com.intellijoy.android.phonics"),
    ABC_KIDS_HANDWIRTING_HWTP("com.teachersparadise.fingertracing004"),
    EPIC("com.getepic.Epic"),
    ANIMAL_SOUNDS("com.premiumsoftware.animalsoundsandphotos"),
    F2B("air.com.myf2b.f2benterprise"),
    SKETCH_A_SONG("com.ohnineline.sas.kids"),
    ANTONYMS("com.sinyee.babybus.antonym"),
    BLOCK_PUZZLE("biz.mtoy.blockpuzzle.revolution"),
    CURIOUS_KID_TOY_CAR("com.GalanteLabs.CuriousKidToyCar"),
    DRAW_FOR_KIDS("com.iskander.drawforkids"),
    FINGER_PAINT("air.com.inclusive.fingerpaint"),
    FLOW_FREE("com.bigduckgames.flow"),
    FIREWORKS("com.bigduckgames.fireworksarcade"),
    KIDS_ABC_TRAINS_LITE("com.intellijoy.abc.trains.lite"),
    KIDS_STICKERS_LITE("com.cocoabox.android.kidsstickerslite"),
    LUMIKIDS_BACKYARD("com.lumikids.backyard"),
    LUMIKIDS_PARK("com.lumikids.park"),
    MARBLE_MANIA("au.com.espace.marbleMania"),
    Numeros_0_10_Spanish_Numbers("com.teachersparadise.numeros"),
    READY_ROSIE("com.pli.ReadyRosie"),
    SHADOW_PLAY("com.joobarr.shadowplay"),
    SHAPES_MOSAIC_PUZZLE("com.iabuzz.akp.MozaicPuzzle"),
    WALK_BAND("com.gamestar.pianoperfect"),
    WASTE_SORTING("com.sinyee.babybus.garbage"),
    FACEBOOK("com.facebook.katana");


    private final String name;

    private AppNames(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return otherName != null && name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }
}
