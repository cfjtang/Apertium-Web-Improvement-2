#define PURPLE_PLUGINS

#include <pidgin.h>
#include <prpl.h>
#include <version.h>
#include <gtkplugin.h>
#include <gtkutils.h>
#include <plugin.h>
#include <prefs.h>
#include <signals.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <stdlib.h>

#include <dbus/dbus-glib.h>

static DBusGConnection *connection;
static DBusGProxy* proxy;

static const char* TRANSLATE_SERVICE_NAME = "org.apertium.mode";
static const char* TRANSLATE_OBJECT_PATH = "/";

#define APERTIUM_PLUGIN_ID "apertium-translator"

static void translate_message( char** message )
{
	char *pair = "en-es";
	GHashTable *hash = g_hash_table_new (g_str_hash, g_str_equal);
	GError *error;
	error = NULL;
	int msglen = strlen( *message );
	char* translated;

	if(!dbus_g_proxy_call(proxy, "translate", &error,
				G_TYPE_STRING, pair,
				DBUS_TYPE_G_STRING_STRING_HASHTABLE, hash,
				G_TYPE_STRING, *message,
				G_TYPE_INVALID,
				G_TYPE_STRING, &translated,
				G_TYPE_INVALID)) {
		if( error->domain == DBUS_GERROR &&
				error->code == DBUS_GERROR_REMOTE_EXCEPTION )
			g_printerr ("Caught remote method exception %s: %s",
					dbus_g_error_get_name( error ),
					error->message);
		else
			g_printerr ("Error: %s\n", error->message);
		g_error_free (error);
		return;
	}
	g_printerr ("Translation: %s\n", translated);

	g_free( *message );
	*message = translated;
}

static gboolean writing_im_msg( PurpleAccount *account, const char *who, char **message, PurpleConversation *conv, PurpleMessageFlags flags, gpointer dontcare )
{
	if( flags & PURPLE_MESSAGE_SEND ) {
		PidginConversation* gtkconv = PIDGIN_CONVERSATION( conv );
		if( !gtkconv )
			return FALSE;
		translate_message( message );
	}
	return TRUE;
}

static void translate_message_im( PurpleAccount* account, char* who,
		char** message, gpointer dontcare )
{
	translate_message( message );
}

static gboolean plugin_load( PurplePlugin* plugin )
{
	g_type_init ();
	GError *error;
	error = NULL;

	connection = dbus_g_bus_get (DBUS_BUS_SESSION, &error);
	if(connection == NULL)
		return FALSE;

	proxy = dbus_g_proxy_new_for_name (connection,
			TRANSLATE_SERVICE_NAME,
			TRANSLATE_OBJECT_PATH,
			"org.apertium.Translate");

	if(proxy == NULL)
		return FALSE;

	void* conv_handle = purple_conversations_get_handle();
	purple_signal_connect( conv_handle, "sending-im-msg",
			plugin, PURPLE_CALLBACK(translate_message_im), NULL );

	return TRUE;
}

static gboolean plugin_unload( PurplePlugin* plugin )
{

	return TRUE;
}

static PurplePluginPrefFrame* get_plugin_pref_frame(PurplePlugin *plugin)
{
	PurplePluginPrefFrame *frame;
	PurplePluginPref *pref;

	frame = purple_plugin_pref_frame_new();
	pref = purple_plugin_pref_new_with_label( "Apertium Translator" );
	purple_plugin_pref_frame_add(frame, pref);

	pref = purple_plugin_pref_new_with_name_and_label( "/plugins/gtk/apertium/lang", "Active language:" );
	purple_plugin_pref_set_type( pref, PURPLE_PLUGIN_PREF_CHOICE );
	purple_plugin_pref_add_choice( pref, "en-es", (void*)"" );
	purple_plugin_pref_frame_add( frame, pref );

	return frame;
}

static PurplePluginUiInfo prefs_info = {
	get_plugin_pref_frame,
	0,
	NULL,
	NULL,
	NULL,
	NULL,
	NULL
};

static PurplePluginInfo info =
{
	PURPLE_PLUGIN_MAGIC,								/* magic */
	PURPLE_MAJOR_VERSION,								/* major version */
	PURPLE_MINOR_VERSION,								/* minor version */
	PURPLE_PLUGIN_STANDARD,								/* type */
	PIDGIN_PLUGIN_TYPE,									/* ui requirements */
	0,													/* flags */
	NULL,												/* dependencies */
	PURPLE_PRIORITY_DEFAULT,							/* priority */
	APERTIUM_PLUGIN_ID,									/* id */
	"Apertium Plugin",									/* name */
	"0.1",												/* version */
	"Apertium Plugin for translation",			        /* summary */
	"On-line translator form es to ca",					/* description */
	"Isaac Clerencia <isaac@warp.es>\nJavier Setoain <javier@sunipx1.dacya.ucm.es>\n",			/* author */
	"http://apertium.sourceforge.net",		            /* homepage */
	plugin_load,										/* load function */
	plugin_unload,										/* unload function */
	NULL,												/* destroy */

	NULL,												/* extra info */
	&prefs_info,										/* prefs info */
	NULL,												/* actions */
	NULL,												/* reserved 1 */
	NULL,												/* reserved 2 */
	NULL,												/* reserved 3 */
	NULL												/* reserved 4 */
};

static void init_plugin( PurplePlugin* plugin )
{
	purple_prefs_add_none("/plugins/gtk/");
	purple_prefs_add_none("/plugins/gtk/apertium");
	purple_prefs_add_bool("/plugins/gtk/apertium/enabled", FALSE);
	purple_prefs_add_string("/plugins/gtk/apertium/lang", "en-es");
}

PURPLE_INIT_PLUGIN( apertium, init_plugin, info );
