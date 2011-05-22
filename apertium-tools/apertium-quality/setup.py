import distribute_setup
distribute_setup.use_setuptools()

from setuptools import setup, find_packages
from os import listdir

setup(
	name = "apertium-quality",
	version = "0.0",
	packages = find_packages(),
	scripts = [ "bin/" + i for i in listdir("bin") if i[0] != '.' ],
	install_requires = ['pyyaml>=3.0'],

	author = "Brendan Molloy",
	author_email = "brendan@bbqsrc.net",
	description = "Apertium Quality Control Framework",
	license = "CC0",
	keywords = "apertium nlp quality control framework",
)
