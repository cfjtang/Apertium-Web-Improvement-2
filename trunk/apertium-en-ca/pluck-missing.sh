#!/bin/bash
awk -F'^' '{print $3}' |\
awk -F'<' '{print $1}'
