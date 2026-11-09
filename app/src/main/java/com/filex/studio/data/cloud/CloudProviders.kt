package com.filex.studio.data.cloud

class DropboxProvider : UnimplementedCloudProvider() {
    override val id = CloudProviderId.DROPBOX
    override val displayName = "Dropbox"
    override val statusNote =
        "Dropbox has an official HTTP API with OAuth 2.0. Not implemented yet (task B28); no credentials are bundled."
}

class MediaFireProvider : UnimplementedCloudProvider() {
    override val id = CloudProviderId.MEDIAFIRE
    override val displayName = "MediaFire"
    override val statusNote =
        "Official API availability and supported sign-in method must be verified before implementation (task B29)."
}

class TeraBoxProvider : UnimplementedCloudProvider() {
    override val id = CloudProviderId.TERABOX
    override val displayName = "TeraBox"
    override val statusNote =
        "Official API availability and supported sign-in method must be verified before implementation (task B30)."
}
