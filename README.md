# TownyWarps - A Towny Addon  
## Information  
Allows town mayors to create more spawn points in addition to the `/town spawn`  
Server admins can limit how many warps can be created, and an initial & exponential growth factor for warp costs  

Town mayors can choose whom to allow to teleport to each warp.  
**permLevel:** `resident` | `outsider`  
`resident` → Must be resident of the town the warp is in  
`outsider` → Must be a resident of any town  

### Commands  
`/town warp <town> <warp>` - Teleport to selected warp  
`/town warps add <name> <permLevel>` - Create warp with selected name & permission level  
`/town warps remove <name>` - Delete selected warp  
`/town warps list <town>` - List all existing warps in selected town  
`/town warps level <warp> <permLevel>` - Update permission level for selected warp  
`/town warps info <town> <warp>` - Get info about selected warp  

### Permissions  
`townywarps.warp.use`  
- Allows teleporting to warps, listing warps, and fetching warp info  
- Default permission  

`townywarps.warp.create`  
- Allows creating warps (Recommended: Mayor only)  

`townywarps.warp.delete`  
- Allows deleting warps (Recommended: Mayor only)  

`townywarps.warp.level`  
- Allows changing warp access level (Recommended: Mayor only)  

### Config  
`maxwarps`  
Maximum warps allowed per town  
Set to `-1` to disable limit  
Default: `5`  

`firstWarpCost`  
Initial cost for first warp  
Default: `3`  

`warpPriceMultiplier`  
Exponential price increase multiplier  
Set to `1` to have all warps priced at `firstWarpCost`  
Default: `2`  
