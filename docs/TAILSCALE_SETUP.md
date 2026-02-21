# Tailscale Setup Guide for VPS Deployment

This guide explains how to set up Tailscale for secure, private access to your VPS and how to integrate it with GitHub Actions for automated deployments.

## 1. Install Tailscale on your VPS

Follow the steps to install Tailscale on your Linux server:

```bash
# Install Tailscale
curl -fsSL https://tailscale.com/install.sh | sh

# Start Tailscale and log in
sudo tailscale up
```

Follow the link provided in the terminal to authenticate your server.

## 2. Verify SSH Access via Tailscale

Once Tailscale is running, you can access your VPS using its Tailscale IP or MagicDNS name.

1.  **Get the Tailscale IP**:
    ```bash
    tailscale ip -4
    ```
2.  **Test connection from your personal laptop**:
    ```bash
    ssh <vps-user>@<tailscale-ip>
    ```

## 3. Configure Tags & Groups

In the [Tailscale Admin Console](https://login.tailscale.com/admin/machines):

1.  **Create a Tag**: Assign a tag like `tag:github-action` to your VPS. This allows you to manage permissions specifically for CI/CD runners.
2.  **Edit ACLs**: Ensure your ACLs allow the `github-action` tag to access the VPS.

Example ACL:

```json
"acls": [
  { "action": "accept", "src": ["tag:github-action"], "dst": ["tag:vps:22"] }
]
```

## 4. Create an OAuth Client

To allow GitHub Actions to connect to your Tailnet securely:

1.  Go to **Settings** > **OAuth Clients** in the Tailscale Admin Console.
2.  Click **Generate OAuth Client**.
3.  Select **Devices** scope (Read/Write).
4.  Optionally, specify the tags the client should use (e.g., `tag:github-action`).
5.  Copy the **Client ID** and **Client Secret**.

## 5. Configure GitHub Secrets

Add the following secrets to your GitHub repository (**Settings** > **Secrets and variables** > **Actions**):

| Secret Name          | Description                                   |
| :------------------- | :-------------------------------------------- |
| `TS_OAUTH_CLIENT_ID` | The ID of the OAuth client created in Step 4. |
| `TS_OAUTH_SECRET`    | The secret for the OAuth client.              |
| `VPS_SSH_KEY`        | Private SSH key for the VPS.                  |
| `VPS_USER`           | Username on the VPS (e.g., `deploy`).         |
| `VPS_HOST`           | Tailscale IP or MagicDNS of your VPS.         |

## 6. Workflow Configuration

The project uses the `tailscale/github-action` in the deployment workflow:

```yaml
- name: Connect to Tailscale
  uses: tailscale/github-action@v4
  with:
    oauth-client-id: ${{ secrets.TS_OAUTH_CLIENT_ID }}
    oauth-secret: ${{ secrets.TS_OAUTH_SECRET }}
    tags: tag:github-action
```

## Resources

- [Tailscale Documentation](https://tailscale.com/docs)
- [Tailscale GitHub Action](https://github.com/tailscale/github-action)
