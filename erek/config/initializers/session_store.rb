# Be sure to restart your server when you modify this file.

# Your secret key for verifying cookie session data integrity.
# If you change this key, all old sessions will become invalid!
# Make sure the secret is at least 30 characters and all random, 
# no regular words or you'll be exposed to dictionary attacks.
ActionController::Base.session = {
  :key         => '_erek_session',
  :secret      => '13af7514b571ac7a553d7a73b3cd2fa851287773d229eae1f6945feb3851512afbf2832d79fca451a1141644dd46e1ab3d4e7b4036458ac0b266cf30997da242'
}

# Use the database for sessions instead of the cookie-based default,
# which shouldn't be used to store highly confidential information
# (create the session table with "rake db:sessions:create")
# ActionController::Base.session_store = :active_record_store
