$FolderYear = '2014'
$FolderToSearchOn = 'C:\Users\craig.nelson\Documents\Alfresco\Migration\Invoices\2014'

$a = Get-ChildItem $FolderToSearchOn
#$fileContent = Import-Csv C:\Users\Administrator\Downloads\Projectnumber1.txt 

foreach($item in $a){
Write-Output $item.Name

if($item.Name){
$Folder = $item.Name.Substring(0, $item.Name.IndexOf("_"))
#Write-Output $Folder
}
#$CSV = $fileContent | Where-Object {$_.ProjectNumber -eq $Last5Digits}
#Write-Output $CSV.Address

#Write-Output $CSV.Address
$folderLocation = $item.FullName
$newLocation = 'C:\Users\craig.nelson\Documents\Alfresco\Migration\Invoices-final\' +$item.Name.SubString(0,1).ToUpper() + '\' + $Folder + '\' + $FolderYear
$newLocationFull = 'C:\Users\craig.nelson\Documents\Alfresco\Migration\Invoices-final\' +$item.Name.SubString(0,1).ToUpper() + '\' + $Folder + '\' + $FolderYear + '\' + $item.Name
Write-Output $newLocation
try{

if (!(Test-Path -path $newLocation)) {New-Item $newLocation -Type Directory}

Move-Item -path $folderLocation $newLocationFull -Force
} catch {
Write-Output "Unable to Move file from " $folderLocation " To " $newLocation
}
#}
}