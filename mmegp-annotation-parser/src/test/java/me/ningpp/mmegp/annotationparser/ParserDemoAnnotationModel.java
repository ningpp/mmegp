package me.ningpp.mmegp.annotationparser;

public record ParserDemoAnnotationModel(boolean aaBoolean, boolean abBoolean, byte acByte, byte adByte, char aeChar,
                                        char afChar, short agShort, short ahShort, int aiInt, int ajInt, long akLong,
                                        long alLong, float amFloat, float anFloat, double aoDouble, double apDouble,
                                        Class<? extends java.time.temporal.Temporal> aqClass,
                                        Class<? extends java.time.temporal.Temporal> arClass,
                                        ParserDemoEnum[] asEnum,
                                        ParserDemoEnum[] atEnum,
                                        ParserDemoEnum auEnum, ParserDemoEnum avEnum,
                                        boolean[] awBoolean, boolean[] axBoolean, byte[] ayByte, byte[] azByte,
                                        char[] baChar, char[] bbChar, short[] bcShort, short[] bdShort, int[] beInt,
                                        int[] bfInt, long[] bgLong, long[] bhLong, float[] biFloat, float[] bjFloat,
                                        double[] bkDouble, double[] blDouble,
                                        ParserDemoInnerAnnotationModel[] bmInner,
                                        ParserDemoInnerAnnotationModel bnInner) {
}
