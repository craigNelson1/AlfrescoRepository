$a = Get-ChildItem 'C:\Users\Administrator\Desktop\Project Jackets2\Production\' -recurse | Where-Object {$_.PSIsContainer -eq $True}
$fileContent = Import-Csv C:\Users\Administrator\Downloads\Projectnumber1.txt 

foreach($item in $a){
$Last5Digits = $item.Name.Substring($item.Name.Length-5,5)
$CSV = $fileContent | Where-Object {$_.ProjectNumber -eq $Last5Digits}
Write-Output $CSV.Address

#Write-Output $CSV.Address
$newName = $item.FullName + "."+$CSV.Address
try{
Rename-Item $item.FullName $newName
} catch {
Write-Output "Unable to Rename file from " $item.FullName " To " $newName
}
#}
}