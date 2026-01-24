def _hytale_repo_impl(ctx):
    # 1. Locate the Workspace Root
    # We resolve the path to the MODULE.bazel file in the main repository (@//)
    # and get its directory name. This gives us the absolute path to your project root.
    workspace_root = ctx.path(Label("@//:MODULE.bazel")).dirname

    # 2. Define the local path
    # We construct the path object pointing to .local-assets/HytaleServer.jar
    local_jar = workspace_root.get_child(".local-assets").get_child("HytaleServer.jar")

    # 3. Check and Act
    if local_jar.exists:
        print("DEBUG: Found local Hytale JAR at {}. Using it.".format(local_jar))
        # Symlink the local file into the external repository
        ctx.symlink(local_jar, "hytale.jar")
    else:
        print("DEBUG: Local JAR not found at {}. Downloading...".format(local_jar))
        # Download from the remote URL
        ctx.download(
            url = "https://artifacts.yakovliam.com/HytaleServer.jar",
            output = "hytale.jar",
            # Highly Recommended: Add sha256 here if the remote file is static
            # sha256 = "..." 
        )

    # 4. Generate the BUILD file
    # This makes the jar available as a java_import target
    ctx.file("BUILD", """
java_import(
    name = "server",
    jars = ["hytale.jar"],
    visibility = ["//visibility:public"],
)
""")

# Define the repository rule
hytale_repository = repository_rule(
    implementation = _hytale_repo_impl,
    # 'environ' ensures the rule rebuilds if specific env vars change, 
    # but detecting a new file usually requires a 'bazel sync' or a Clean.
)

# Define the module extension
def _hytale_extension_impl(ctx):
    hytale_repository(name = "remote_hytale_server")

hytale_ext = module_extension(
    implementation = _hytale_extension_impl,
)