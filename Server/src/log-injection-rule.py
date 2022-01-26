from flask import app, current_app, request
from django.http import HttpResponse
import logging
logger = logging.getLogger(__name__)

def conformant_0():
    safe_input = "SAFE_STRING"
    logger.info("%s", safe_input)

def non_conformant_0():
    input = raw_input("Enter data: ")
    logger.error("%s", input)

# flask
@app.route('/log')
def non_conformant_1():
    input = request.args.get('input')
    current_app.logger.error("%s", input)

def non_conformant_2():
    key = request.form.get("key")
    data = request.values["data"]
    current_app.logger.error("key:%s, data:%s", key, data)

def conformant_1():
    input = request.args.get('input')
    if input.isalnum():
        current_app.logger.warning("%s", input)

#Django
def non_conformant_3(request):
    input = request.GET.get("input")
    logger.error("%s", input)

def non_conformant_4(request):
    key = request.POST["key"]
    data = request.POST["data"]
    logger.error("key:%s, data:%s", key, data)

def conformant_2(request):
    input = request.GET.get("input")
    if input.isalnum():
        logger.warning("%s", input)

# inter-procedural cases:
def sanitize(str):
    return str if str.isalnum() else "SAFE_STRING"

def sink(str):
    logger.error("%s", str)

def non_conformant_5():
    sink(raw_input("Enter data: "))

def conformant_3():
    sink(sanitize(raw_input("Enter data: ")))

# across different source files
import log-helper
def non_conformant_across_files():
    log_data(raw_input("Enter data: "))

# Sanitizer: cast to numeric
def conformant_convert_numeric():
    logger.info("int = %d", int(raw_input("Enter a number: ")))
    logger.info("float = %f", float(raw_input("Enter a number: ")))

# Sanitizer: list membership check
def get_boolean(val):
    return val in ['True', 'true', '1', 'yes']
def conformant_list_check_sanitize():
    logger.info("choice = %d", get_boolean(raw_input("Continue? [yes/no]: ")))

# Validator: list membership check
def conformant_list_check_validate():
    choice = raw_input("Continue? [yes/no]: ")
    if choice in ['yes', 'no']:
        logger.info("choice = %s", choice)

# Validator: equality comparison
def conformant_equality_check():
    choice = input("Continue? [c]Confirm or [a]Abort: ")
    if choice == 'c':
        logger.info("Choice=%s. continuing...", choice)
    elif choice == 'a':
        logger.info("Choice=%s. continuing...", choice)

# Validator: custom validation
def conformant_custom_validation():
    input = request.GET.get("input")
    if is_valid_text(input):
        logger.info("%s", input)

# Validator: regex match
def conformant_regex_match():
    filename = request.files.get('file')['filename']
    if re.match(r'^[\w_ -\.]+$', filename):
        logger.info("Uploading file %s", filename)

# Validator: successful auth- username is validated w.r.t log injection, but still a risk of info leak.

# Sanitizer: file property
def conformant_file_type():
    file = request.files.get('file')
    logger.error("Unsupported media type: %s", file.content_type)

# Exclude source: http request metadata
def conformant_trusted_metadata():
    logger.info("Request type %s by user %s from %s",
                request.META[self.message_type_header],
                request.META["REMOTE_USER"],
                request.META.get('REMOTE_ADD'))
