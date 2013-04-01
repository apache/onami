package org.apache.onami.converters.i18n;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.onami.converters.core.AbstractConverter;
import org.kohsuke.MetaInfServices;

import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Converter implementation for {@code java.util.Calendar} and {@code java.util.Date}.
 */
@MetaInfServices( Module.class )
public final class DateConverter
    extends AbstractConverter<Date>
{

    private final List<String> patterns = new ArrayList<String>();

    private Locale locale;

    private TimeZone timeZone;

    public DateConverter()
    {
        // ISO date formats
        addPattern( "yyyy" );
        addPattern( "yyyy-MM" );
        addPattern( "yyyy-MM-dd" );
        addPattern( "yyyy-MM-dd'T'hh:mmZ" );
        addPattern( "yyyy-MM-dd'T'hh:mm:ssZ" );
        addPattern( "yyyy-MM-dd'T'hh:mm:ss.sZ" );
    }

    public void setLocale( Locale locale )
    {
        this.locale = locale;
    }

    public void setTimeZone( TimeZone timeZone )
    {
        this.timeZone = timeZone;
    }

    public void addPattern( String pattern )
    {
        this.patterns.add( pattern );
    }

    /**
     * {@inheritDoc}
     */
    public Object convert( String value, TypeLiteral<?> toType )
    {
        Exception firstEx = null;
        for ( String pattern : patterns )
        {
            try
            {
                DateFormat format;
                if ( locale != null )
                {
                    format = new SimpleDateFormat( pattern, locale );
                }
                else
                {
                    format = new SimpleDateFormat( pattern );
                }
                if ( timeZone != null )
                {
                    format.setTimeZone( timeZone );
                }
                format.setLenient( false );
                Date date = parse( value, format );

                if ( Calendar.class == toType.getType() )
                {
                    Calendar calendar = null;
                    if ( locale == null && timeZone == null )
                    {
                        calendar = Calendar.getInstance();
                    }
                    else if ( locale == null )
                    {
                        calendar = Calendar.getInstance( timeZone );
                    }
                    else if ( timeZone == null )
                    {
                        calendar = Calendar.getInstance( locale );
                    }
                    else
                    {
                        calendar = Calendar.getInstance( timeZone, locale );
                    }
                    calendar.setTime( date );
                    calendar.setLenient( false );
                    return calendar;
                }

                return date;
            }
            catch ( RuntimeException ex )
            {
                if ( firstEx == null )
                {
                    firstEx = ex;
                }
            }
        }

        throw new IllegalArgumentException( "Error converting '"
                                            + value
                                            + "' using  patterns "
                                            + patterns,
                                            firstEx );
    }

    private Date parse( String value, DateFormat format )
    {
        ParsePosition pos = new ParsePosition( 0 );
        Date parsedDate = format.parse( value, pos ); // ignore the result (use the Calendar)

        if ( pos.getErrorIndex() >= 0 || pos.getIndex() != value.length() || parsedDate == null )
        {
            String msg = "Error converting '" + value + "'";
            if ( format instanceof SimpleDateFormat )
            {
                msg += " using pattern '" + ( (SimpleDateFormat) format ).toPattern() + "'";
            }
            throw new IllegalArgumentException( msg );
        }

        return parsedDate;
    }

}
