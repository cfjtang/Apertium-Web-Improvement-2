import sys;
import socket;
import string;
import time;
import commands;
from time import strftime, gmtime;

## See http://wiki.apertium.org/wiki/Eleda
## start with the command
## python eleda.py

HOST='irc.freenode.net';
PORT=6667;
NICK='eleda';
IDENT='eleda';
REALNAME='Eleda a robot translator - type @help for help';
OWNER='spectie';
readbuffer='';
PASS='PUT_YOUR_PASSWORD_HERE';

## DIR='/home/fran/svnroot/apertium/apertium-'

channel_list = ['#apertium'];

# contains list of hashes like 'requester, user, direction'
following = {};


pairs = {};

##PREFIX_UNICODE='LD_LIBRARY_PATH=/home/fran/svnroot/local/unicode/lib/ PATH=/home/fran/svnroot/local/unicode/bin:$PATH'
##APERTIUM_CMD=PREFIX_UNICODE  + ' /home/fran/svnroot/local/unicode/bin/apertium '
APERTIUM_CMD='apertium'

## need to check for ' and " and etc.

def get_pairs(): #{
	cmd = APERTIUM_CMD + ' xxxxxxxxxxxxxxxxs1khd2xxxxx';
	
	for val in commands.getoutput(cmd).replace('Error: Mode xxxxxxxxxxxxxxxxs1khd2xxxxx does not exist. Try one of:\n', '').split('\n'):
		print val;
		pairs[val.strip()] = val.strip();
	#}

#}


def translate(string, direction): #{
	translation = 'test';
	message = string.split(':');

	cmd = 'echo "' + message[2].strip() + '" | ' + APERTIUM_CMD + ' ' +  direction;
	
	print cmd;
	translation = commands.getstatusoutput(cmd)[1];
	return translation;
#}

def send_help(user): #{
 	mesg = 'NOTICE ' + user + ' :' + 'Apertium translation bot. See http://wiki.apertium.org/wiki/Eleda' + '\n';
	s.send(mesg);
 	mesg = 'NOTICE ' + user + ' :' + '@follow <nick> <direction> - Translate a user.' + '\n';
	s.send(mesg);
 	mesg = 'NOTICE ' + user + ' :' + '@unfollow <nick>           - Stop translating a user.' + '\n';
	s.send(mesg);
 	mesg = 'NOTICE ' + user + ' :' + '@following                 - List users currently being followed.' + '\n';
	s.send(mesg);
 	mesg = 'NOTICE ' + user + ' :' + '@list                      - List available pairs.' + '\n';
	s.send(mesg);
 	mesg = 'NOTICE ' + user + ' :' + '@help                      - Display this help.' + '\n';
	s.send(mesg);
#}

def follow_user(followee, follower, direction): #{
	if followee == '' or follower == '' or direction == '': #{
		mesg = 'NOTICE ' + follower + ' :Insufficient arguments, try @help'  + '\n';
		s.send(mesg);
		return;
	#}

	if not pairs.has_key(direction): #{
		mesg = 'NOTICE ' + follower + ' :Invalid pair.' + '\n';
		s.send(mesg);
		return;
	#}

	#print 'followee: ' + followee + '; follower: ' + follower + '; direction: ' + direction;
	follow_item = (follower, followee, direction);

	if not following.has_key(followee): #{
		following[followee] = [];
	#}

	following[followee].append((follow_item));
	mesg = 'NOTICE ' + follower + ' :following ' + followee + ', direction ' + direction + '\n';
	s.send(mesg);
#}

def unfollow_user(followee, follower): #{
	if following.has_key(followee.strip()): #{
		for user in following[followee.strip()]: #{
			if follower == user[0]: #{
				following[followee.strip()].remove(user);
				mesg = 'NOTICE ' + follower + ' :unfollowing ' + followee + '\n';
				s.send(mesg);
			#}
		#}
	#}
#}

def clear_following(follower): #{
	print 'Clearing following for ' + follower;
	for val in following.values(): #{
		for item in val: #{
			if follower.strip() == item[0]: #{
				unfollow_user(item[1], follower);
			#}
		#}
	#}
#}

def list_following(follower): #{
	mesg = 'NOTICE ' + follower + ' :Currently following: ';
	for val in following.values(): #{
		for item in val: #{
			if follower.strip() == item[0]: #{
				mesg = mesg + item[1] + ' (' + item[2] + ')' + ' ';
			#}
		#}
	#}

	mesg = mesg + '\n';
	s.send(mesg);
#}

def parsemsg(msg): #{
	complete = msg[1:].split(':',1);
	info = complete[0].split(' ');
	msgpart = complete[1];
	sender = info[0].split('!');

	t = strftime('%H:%M:%S', gmtime());
	#print '[' + t + '] ' + info[2] + ' <' + sender[0] + '> ' + msgpart.strip();

	if following.has_key(sender[0].strip()) and  msgpart[0] != '@': #{
		for user in following[sender[0].strip()]: #{
			translation = translate(msg, user[2]);
			mesg = 'NOTICE ' + user[0] + ' :' +  translation + '\n';
			print mesg;
			message = string.split(':');
			if translation.find('Error: Malformed input stream.') == -1: #{
				# if the message does not contain any translated words, screw it.
				testmsg =  msgpart.strip();
				translation = translation.replace('*', '');
				if translation.find(testmsg) == -1: #{
					s.send(mesg);
				#}
			#}
		#}
	#}

	if msgpart[0] == '@': #{
		cmd = msgpart[1:].split(' ');
		cmd[0] = cmd[0].strip();
		if cmd[0] == 'follow': #{
			if len(cmd) <= 2: #{
				send_help(sender[0]);
				return;
			#}
			follow_user(cmd[1], sender[0], cmd[2].strip());
		#}
		if cmd[0] == 'unfollow': #{
			if len(cmd) <= 1: #{
				send_help(sender[0]);
				return;
			#}
			unfollow_user(cmd[1], sender[0]);
		#}
		if cmd[0] == 'help': #{
			send_help(sender[0]);
		#}
		if cmd[0] == 'following': #{
			list_following(sender[0]);
		#}
		if cmd[0] == 'list': #{
			mesg = 'NOTICE ' + sender[0] + ' :' + 'Available translation pairs:' + '\n';
			s.send(mesg);
			mesg = 'NOTICE ' + sender[0] + ' :';
			for key in pairs.keys():  #{
				mesg = mesg + key + ' ';
			#}
			mesg = mesg + '\n';
			s.send(mesg);
		#}
	#}    
#}

#
# 	main()
#

if __name__ == "__main__": #{

	get_pairs();
	s = socket.socket();
	s.settimeout(3600);
	s.connect((HOST, PORT));
	s.send('NICK ' + NICK + '\n');
	s.send('USER ' + IDENT + ' ' + HOST + ' bla :' + REALNAME + '\n');

	# main loop
	while 1: #{
		try: #{
			line = s.recv(512);
		#}
		except socket.timeout: #{
			s.shutdown(socket.SHUT_RDWR);
			s = socket.socket();
			s.connect((HOST, PORT));
			s.send('NICK ' + NICK + '\n');
			s.send('USER ' + IDENT + ' ' + HOST + ' bla :' + REALNAME + '\n');
		#}

			
		print line; 

		if line.find('Welcome to the freenode IRC Network')!=-1: #{
			for channel in channel_list: #{
				s.send('JOIN ' + channel + '\n'); #Join a channel
				s.send('I am a robot translator. Type @help for info on how to use me\n');  Francis, OK??
			#}
		#}

		# nickserv up
		if line.find('This nickname is owned by someone else') != -1: #{
			s.send('PRIVMSG NickServ :IDENTIFY ' + PASS + '\n');
		#}

		if line.find('PART') != -1: #{
			mesg = line.split(':');
			mesg2 = mesg[1].split('!');

			clear_following(mesg2[0]);
		#}
			
		if line.find('PRIVMSG')!=-1: #{
			parsemsg(line);
			line = line.rstrip(); #remove trailing 'rn'
			line = line.split();

			if(line[0] == 'PING'): #{
				s.send('PONG ' + line[1] + '\n');
			#}
		#}
	#}
#}
