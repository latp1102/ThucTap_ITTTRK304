import bcrypt

# Generate bcrypt hash for Admin@1234 with 10 rounds (matches Spring BCryptPasswordEncoder(10))
hashed = bcrypt.hashpw(b'Admin@1234', bcrypt.gensalt(rounds=10))
print(f"Generated hash: {hashed.decode()}")

# Also generate for User@1234
hashed_user = bcrypt.hashpw(b'User@1234', bcrypt.gensalt(rounds=10))
print(f"User hash: {hashed_user.decode()}")