/*
 *  ApertiumServer. Highly scalable web service implementation for Apertium.
 *  Copyright (C) 2009  Víctor Manuel Sánchez Cartagena
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.gsoc.apertium.translationengines.router.ws;

/**
 * JSON Constants. Mainly names of fields
 *
 * @author vmsanchez
 */
public interface Constants {

    public static final String JSON_RESPONSEDATA="responseData";
    public static final String JSON_RESPONSESTATUS="responseStatus";
    public static final String JSON_RESPONSEDETAILS="responseDetails";
    public static final String JSON_TRANSLATEDTEXT="translatedText";

    public static final String JSON_SOURCELANG="sourceLanguage";
    public static final String JSON_TARGETLANG="targetLanguage";

    public static final String JSON_DEFAULT_RESPONSE="{\"responseData\":null,\"responseDetails\":\"Unexpected error\",\"responseStatus\":500}";
    public static final String JSON_DEFAULT_RESPONSE_DATA="null";

}
