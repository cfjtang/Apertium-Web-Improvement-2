import sys;
# 
# For bulk uploading tasks please use your favorite spreadsheet editor that can export data in CSV format. Structure your columns in the following order.
# 
#     Title of the task.
#     Description, for markup use HTML.
#     Time to complete in hours, as integer.
#     Usernames of mentors for this task, comma separated.
#     Task types, one or more of the allowed types (Code, Documentation/Training, Outreach/Research, Quality Assurance, User Interface), comma separated.
#     Task tags, any custom tags you want to add, comma separated.
# 
# The field separator is the comma character.
# If your field contains commas, use double quotes ("field with a , comma") to quote the field content.
# If your field contains double quotes, escape as two double quotes ("field with "" double quotes"). 
# 
tasksf = open('tasks.txt');
descsf = open('descriptions.txt');
langsf = open('languages.txt');

reg_mentors = ['jnw', 'unhammer', 'ftyers', 'mlforcada', 'fpetkovski', 'hperadin', 'jimregan', 'quirille', 'nvohra', 'selimcan', 'youssef_oualmakran', 'zx48', 'tapirine'];

each = int(sys.argv[1]);

class Task: #{
	tid = -1;
	title = '';
	multi = False;
	description = '';
	time = -1;
	mentors = '';
	ttype = '';
	tags = '';

	def __init__(self, tid, title, multi, time, mentors, ttype, tags): #{
		self.tid = tid;
		self.title = title;
		self.multi = multi;
		self.time = time;
		self.tags = tags;

		self.mentors = mentors;

		# Task types, one or more of the allowed types (Code, 
		# Documentation/Training, Outreach/Research, Quality Assurance, User Interface), comma separated.
		if ttype.count(',') > 0: #{
			self.ttype = '';
			for t in ttype.split(','): #{
				t = t.strip();
				if t == 'code': #{
					self.ttype = self.ttype + 'Code,';
				elif t == 'research': #{
					self.ttype = self.ttype + 'Outreach/Research,';
				elif t == 'interface': #{
					self.ttype = self.ttype + 'User Interface,';
				elif t == 'documentation': #{
					self.ttype = self.ttype + 'Documentation/Training,';	
				elif t == 'quality': #{
					self.ttype = self.ttype + 'Quality Assurance,';
				#}
			#}
		
			self.ttype = self.ttype.strip(',');

		else: #{
			if ttype == 'code': #{
				self.ttype = 'Code';
			elif ttype == 'research': #{
				self.ttype = 'Outreach/Research';
			elif ttype == 'interface': #{
				self.ttype = 'User Interface';
			elif ttype == 'documentation': #{
				self.ttype = 'Documentation/Training';	
			elif ttype == 'quality': #{
				self.ttype = 'Quality Assurance';
			#}
		#}
	#}
#}

tasks = {};
counter = 0;
for line in tasksf.readlines(): #{

	row = line.strip('\n').split('\t');

	if row[0] == '' or row[0] == 'ID' or row[3] == '': #{
		continue;
	#}

	tid = int(row[0]);
	multi = row[1];
	title = row[2];
	time = int(row[3]);
	mentors = row[4];
	ttype = row[5];
	tags = row[6];

	task = Task(tid, title, multi, time, mentors, ttype, tags);	

	tasks[tid] = task;	

	counter = counter + 1;
#}

for line in descsf.readlines(): #{

	row = line.strip('\n').split('\t');

	if row[0] == '' or row[0] == 'ID' or row[0] == '."': #{
		continue;
	#}

	tid = int(row[0]);
	title = row[1];
	descr = row[2].replace('"', '""');

	if tid in tasks: #{
		tasks[tid].description = descr;
	#}
#}

total = 0;
for line in langsf.readlines(): #{

	row = line.strip('\n').split('\t');

	if row[0] == '' or row[0] == 'AAA' or row[4] == '':  #{
		continue;
	#}

	tid = int(row[2]);
	aaa = row[0];
	bbb = row[1];
	quantity = int(row[4]);	
	xmentors = row[5];

	if tid in tasks: #{
		task = tasks[tid];
		title = task.title;
		description = task.description;
		time = task.time;
		mentors = task.mentors.strip();
		tags = task.tags;
		ttype = task.ttype;
		mentors = mentors + ',' + xmentors;
		if description.strip() == '': #{
			print('[Yes] Missing description for task #' + str(tid), file=sys.stderr);
			continue;
		#}
		if ttype.strip() == '': #{
			print('[Yes] Missing type for task #' + str(tid), file=sys.stderr);
			continue;
		#}
		if tags.strip() == '': #{
			print('[Yes] Missing tags for task #' + str(tid), file=sys.stderr);
			continue;
		#}

		newmentors = '';
		if mentors.count(',') > 0 and mentors.strip() != '': #{
			for m in mentors.split(','): #{
				m = m.strip();
				if m in reg_mentors and m != '': #{
					newmentors = newmentors + m + ',';
				elif m != '': #{
					print('[Yes] Mentors %s for task #%s not registered' % (m, str(tid)), file=sys.stderr);
				#}
			#}
		elif mentors.strip() != '': #{
			if mentors not in reg_mentors: #{
				print('[Yes] Mentors %s for task #%s not registered' % (mentors, str(tid)), file=sys.stderr);
			else: #{
				newmentors = mentors;
			#}
		#}
		if newmentors != '': #{
			mentors = newmentors;
		#}
		if mentors.strip() == '': #{
			print('[Yes] Missing mentors for task #%s: %s' % (str(tid), mentors), file=sys.stderr);
			continue;
		#}

		
		if aaa != '' and bbb != '': #{
			title = title + ' (' + aaa + ' and ' + bbb + ')';	
			description = description.replace('AAA', aaa);
			description = description.replace('BBB', bbb);
			tags = tags + ',' + aaa.lower() + ',' + bbb.lower();
		elif aaa != '' and bbb == '': #{
			title = title + ' (' + aaa + ')';
			description = description.replace('AAA', aaa);
			tags = tags + ',' + aaa.lower();
		#}
		tags = tags.strip(',');
		for i in range(0, quantity): #{
			out = '';
			ltitle = title + ' [' + str(i) + ']';
			out = out + '"' + ltitle + '",';
			out = out + '"' + description + '",';
			out = out + '"' + str(time) + '",';
			out = out + '"' + mentors.strip(',') + '",';
			out = out + '"' + ttype + '",';
			out = out + '"' + tags.lower() + '"';

			print(out);
			total = total + 1;
			if total % each == 0: #{
				print('------------------------------------------------');
			#}
		#}
	#}
#}

for task in tasks: #{
	if tasks[task].multi == 'No': #{
		tid = tasks[task].tid;
		if tasks[task].description.strip() == '': #{
			print('[No]  Missing description for task #' + str(tid), file=sys.stderr);
			continue;
		#}
		if tasks[task].ttype.strip() == '': #{
			print('[No]  Missing type for task #' + str(tid), file=sys.stderr);
			continue;
		#}
		if tasks[task].tags.strip() == '': #{
			print('[No]  Missing tags for task #' + str(tid), file=sys.stderr);
			continue;
		#}
		if tasks[task].mentors.strip() == '': #{
			print('[No]  Missing mentors for task #' + str(tid), file=sys.stderr);
			continue;
		#}
		out = '';
		out = out + '"' + tasks[task].title + '",';
		out = out + '"' + tasks[task].description + '",';
		out = out + '"' + str(tasks[task].time) + '",';
		out = out + '"' + tasks[task].mentors.strip(',') + '",';
		out = out + '"' + tasks[task].ttype + '",';
		out = out + '"' + tasks[task].tags + '"';

		print(out);
		total = total + 1;
		if total % each == 0: #{
			print('------------------------------------------------');
		#}
	#}
#}

print(counter, total, file=sys.stderr);
