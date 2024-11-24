import os
import yaml
from datetime import datetime

def set_default(d, key, default_value):
    if not isinstance(d, dict):
        return
    if key not in d:
        d[key] = default_value

# Read existing publiccode.yaml
try:
    with open("publiccode.yaml", "r") as f:
        data = yaml.safe_load(f)
except FileNotFoundError:
    data = {}

# Convert created_at to date format
created_at_date = datetime.now().strftime('%Y-%m-%d')

# Initialize missing keys with default values
set_default(data, 'publiccodeYmlVersion', "0.4")
set_default(data, 'name', "")
set_default(data, 'url', "")
set_default(data, 'softwareVersion', "")
set_default(data, 'releaseDate', created_at_date)
set_default(data, 'platforms', ["web"])
set_default(data, 'categories', ["it-development"])
set_default(data, 'usedBy', [])
set_default(data, 'roadmap', "")
set_default(data, 'developmentStatus', "development")
set_default(data, 'softwareType', "standalone/web")
set_default(data, 'description', {'en': {}})
set_default(data['description']['en'], 'localisedName', "")
set_default(data['description']['en'], 'shortDescription', "")
set_default(data['description']['en'], 'longDescription', "")
set_default(data['description']['en'], 'documentation', "")
set_default(data['description']['en'], 'apiDocumentation', "")
set_default(data['description']['en'], 'features', [])
set_default(data['description']['en'], 'screenshots', [])
set_default(data['description']['en'], 'videos', [])
set_default(data['description']['en'], 'awards', [])
set_default(data, 'legal', {})
set_default(data['legal'], 'license', "")
set_default(data['legal'], 'mainCopyrightOwner', "")
set_default(data['legal'], 'repoOwner', "")
set_default(data['legal'], 'authorsFile', "")
set_default(data, 'maintenance', {})
set_default(data['maintenance'], 'type', "none")
set_default(data['maintenance'], 'contractors', [])
set_default(data['maintenance'], 'contacts', [])
set_default(data, 'localisation', {})
set_default(data['localisation'], 'localisationReady', False)
set_default(data['localisation'], 'availableLanguages', ["en"])
set_default(data, 'organisation', {})

# Update or append values
if os.environ.get('REPO_NAME'):
    data['name'] = os.environ['REPO_NAME']
if os.environ.get('REPO_URL'):
    data['url'] = os.environ['REPO_URL']
if os.environ.get('REPO_DESC'):
    data['description']['en']['shortDescription'] = os.environ['REPO_DESC']
if os.environ.get('REPO_HOMEPAGE'):
    data['url'] = os.environ['REPO_HOMEPAGE']
if os.environ.get('REPO_LICENSE'):
    data['legal']['license'] = os.environ['REPO_LICENSE']
if os.environ.get('MAIN_COPYRIGHT_OWNER'):
    data['legal']['mainCopyrightOwner'] = os.environ['MAIN_COPYRIGHT_OWNER']
if os.environ.get('REPO_OWNER'):
    data['legal']['repoOwner'] = os.environ['REPO_OWNER']
if os.environ.get('ORGANISATION_NAME'):
    data['organisation']['name'] = os.environ['ORGANISATION_NAME']
if os.environ.get('ORGANISATION_AVATAR'):
    data['organisation']['logo'] = os.environ['ORGANISATION_AVATAR']
if os.environ.get('ORGANISATION_URL'):
    data['organisation']['url'] = os.environ['ORGANISATION_URL']
if os.environ.get('ORGANISATION_DESCRIPTION'):
    data['organisation']['description'] = os.environ['ORGANISATION_DESCRIPTION']
if os.environ.get('SOFTWARE_VERSION'):
    data['softwareVersion'] = os.environ['SOFTWARE_VERSION']
if os.environ.get('ROADMAP'):
    data['roadmap'] = os.environ['ROADMAP']
if os.environ.get('LOCALISATION_READY'):
    data['localisation']['localisationReady'] = os.environ['LOCALISATION_READY']
if os.environ.get('AVAILABLE_LANGUAGES'):
    data['localisation']['availableLanguages'] = os.environ['AVAILABLE_LANGUAGES']


# Write updated publiccode.yaml
with open("publiccode.yaml", "w") as f:
    yaml.safe_dump(data, f)